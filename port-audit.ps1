# port-audit.ps1 - compares the NeoForge 1.21.1 port against the original Fabric code and assets.
# Run from the project root:  pwsh -ExecutionPolicy Bypass -File .\port-audit.ps1 *> port-report.txt
# Read-only: it changes nothing.

$ErrorActionPreference = 'Continue'
$root = (Get-Location).Path
$oldJava = Join-Path $root '_fabric-old\vnap'
$newJava = Join-Path $root 'src\main\java\com\vnap'
$res     = Join-Path $root 'src\main\resources'

function Section($title) { "`n==================== $title ====================" }

Section 'A. Java files in the original with no counterpart in src'
$expectedDropped = @('\client\VillagerNewsModMenu.java')   # Mod Menu has no NeoForge equivalent
Get-ChildItem $oldJava -Recurse -Filter *.java | ForEach-Object {
    $rel = $_.FullName.Substring($oldJava.Length)
    if (-not (Test-Path (Join-Path $newJava $rel))) {
        if ($expectedDropped -contains $rel) { "$rel   (intentionally dropped)" } else { "$rel   <-- NOT PORTED" }
    }
}

Section 'B. Mixin classes present in src but NOT listed in a mixin config'
$listed = @()
foreach ($cfg in 'villager-news-addon-port.mixins.json', 'villager-news-addon-port.client.mixins.json') {
    $p = Join-Path $res $cfg
    if (Test-Path $p) {
        $j = Get-Content $p -Raw | ConvertFrom-Json
        foreach ($k in 'mixins', 'client', 'server') { if ($j.$k) { $listed += @($j.$k) } }
        "config $cfg : mixins=$(@($j.mixins).Count) client=$(@($j.client).Count)"
    } else { "config $cfg : MISSING" }
}
Get-ChildItem (Join-Path $newJava 'mixin') -Recurse -Filter *.java | ForEach-Object {
    $name = $_.BaseName
    if ($listed -notcontains $name) { "$name   <-- present but inactive" }
}

Section 'C. Method names in the original that no longer exist in the port (same file)'
$methodPattern = '^\s*(?:(?:public|protected|private|static|final|abstract|synchronized)\s+)+[\w<>\[\]?,.\s]+?\s+(\w+)\s*\('
function Get-MethodNames($file) {
    Select-String -Path $file -Pattern $methodPattern | ForEach-Object { $_.Matches[0].Groups[1].Value } | Sort-Object -Unique
}
Get-ChildItem $oldJava -Recurse -Filter *.java | ForEach-Object {
    $rel = $_.FullName.Substring($oldJava.Length)
    $new = Join-Path $newJava $rel
    if (Test-Path $new) {
        $oldNames = Get-MethodNames $_.FullName
        $newNames = Get-MethodNames $new
        $missing = @($oldNames | Where-Object { $newNames -notcontains $_ })
        if ($missing.Count -gt 0) { "$rel : " + ($missing -join ', ') }
    }
}

Section 'D. Resource files in the original (main branch) missing from the working tree'
$orig = @(git ls-tree -r --name-only main -- src/main/resources)
$now  = @(Get-ChildItem $res -Recurse -File | ForEach-Object { ($_.FullName.Substring($root.Length + 1)) -replace '\\', '/' })
"original files: $($orig.Count)   current files: $($now.Count)"
$missing = @(Compare-Object $orig $now | Where-Object SideIndicator -eq '<=' | ForEach-Object InputObject)
"missing now: $($missing.Count)"
$missing | Group-Object { Split-Path $_ -Parent } | Sort-Object Count -Descending | Select-Object -First 40 |
    ForEach-Object { '{0,5}  {1}' -f $_.Count, $_.Name }
"-- files added since (not in original): "
$added = @(Compare-Object $orig $now | Where-Object SideIndicator -eq '=>' | ForEach-Object InputObject)
$added | Select-Object -First 30

Section 'E. JSON files that do not parse'
Get-ChildItem $res -Recurse -Filter *.json | ForEach-Object {
    try { Get-Content $_.FullName -Raw | ConvertFrom-Json | Out-Null } catch { "$($_.FullName.Substring($root.Length + 1))" }
}

Section 'F. Model files using the free-rotation format that 1.21.1 rejects'
Get-ChildItem (Join-Path $res 'assets') -Recurse -Filter *.json | Where-Object {
    (Get-Content $_.FullName -Raw) -match '(?s)"rotation"\s*:\s*\{[^}]*"x"\s*:'
} | ForEach-Object { $_.FullName.Substring($root.Length + 1) }

Section 'G. Data files (recipes, tags, loot, advancements) and their ingredient style'
Get-ChildItem (Join-Path $res 'data') -Recurse -File -ErrorAction SilentlyContinue | ForEach-Object {
    $rel = $_.FullName.Substring($root.Length + 1)
    $txt = Get-Content $_.FullName -Raw
    $flag = if ($txt -match '"ingredients"\s*:\s*\[\s*"') { '   <-- string ingredients (1.21.1 needs objects)' } else { '' }
    "$rel$flag"
}

Section 'H. Leftover test files that should not be committed'
Get-ChildItem $res -Recurse | Where-Object { $_.Name -match '\.(off|bak|orig)$' } | ForEach-Object { $_.FullName.Substring($root.Length + 1) }
Get-ChildItem $root -File | Where-Object { $_.Name -match '^(errors|server-errors|sheep2-outline|port-report)\.txt$|problems-report' } | ForEach-Object { $_.Name }

Section 'I. Original startup registrations (for manual comparison)'
$origMain = Join-Path $oldJava 'VillagerNewsAddonPort.java'
if (Test-Path $origMain) {
    Select-String -Path $origMain -Pattern 'register|Registry|Network|Controller|Items|Settings|Catalog|Command|load\(' |
        ForEach-Object { '{0,4}: {1}' -f $_.LineNumber, $_.Line.Trim() }
} else { 'original main class not found' }

Section 'DONE'
