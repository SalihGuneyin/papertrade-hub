Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$normalizedRoot = $projectRoot.ToLowerInvariant()

$processes = Get-CimInstance Win32_Process | Where-Object {
    $_.CommandLine -and (
        $_.CommandLine.ToLowerInvariant().Contains($normalizedRoot) -or
        $_.CommandLine.Contains('com.salihguneyin.papertradehub.PaperTradeHubApplication')
    )
}

if (-not $processes) {
    Write-Output 'No PaperTrade Hub background processes were found.'
    return
}

foreach ($process in $processes) {
    try {
        Stop-Process -Id $process.ProcessId -Force -ErrorAction Stop
        Write-Output ("Stopped PID {0}" -f $process.ProcessId)
    }
    catch {
        Write-Output ("Could not stop PID {0}: {1}" -f $process.ProcessId, $_.Exception.Message)
    }
}

