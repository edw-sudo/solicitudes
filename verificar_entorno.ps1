# =====================================================================
# Verificación del entorno para el Sprint 2 (Java + Spring Boot en VS Code)
# Uso: abre la terminal de VS Code (Ctrl + ñ) y ejecuta:
#   powershell -ExecutionPolicy Bypass -File .\verificar_entorno.ps1
# Opcional, para probar la conexión a Supabase:
#   powershell -ExecutionPolicy Bypass -File .\verificar_entorno.ps1 -SupabaseHost "aws-0-xx.pooler.supabase.com" -Puerto 5432
# =====================================================================
param(
    [string]$SupabaseHost = "",
    [int]$Puerto = 5432
)

$ok = 0; $fallas = 0
function Ok($msg)    { Write-Host "  [OK]    $msg" -ForegroundColor Green;  $script:ok++ }
function Falla($msg) { Write-Host "  [FALTA] $msg" -ForegroundColor Red;    $script:fallas++ }
function Info($msg)  { Write-Host "  [INFO]  $msg" -ForegroundColor Yellow }

Write-Host "`n1. Java (JDK 17 o superior)" -ForegroundColor Cyan
$javaVer = (& java -version 2>&1 | Select-Object -First 1) 2>$null
if ($javaVer) {
    if ($javaVer -match '"(\d+)') {
        $mayor = [int]$Matches[1]
        if ($mayor -ge 17) { Ok "java: $javaVer" } else { Falla "java es version $mayor; se necesita 17 o superior" }
    } else { Info "java encontrado: $javaVer" }
} else { Falla "java no esta instalado o no esta en el PATH" }

$javacVer = (& javac -version 2>&1) 2>$null
if ($javacVer -match 'javac') { Ok "javac (compilador del JDK): $javacVer" }
else { Falla "javac no encontrado: tienes un JRE, instala el JDK completo" }

if ($env:JAVA_HOME) { Ok "JAVA_HOME = $env:JAVA_HOME" }
else { Info "JAVA_HOME no esta definido (VS Code suele detectar el JDK igual)" }

Write-Host "`n2. Visual Studio Code y extensiones" -ForegroundColor Cyan
$code = Get-Command code -ErrorAction SilentlyContinue
if ($code) {
    $ext = & code --list-extensions
    $requeridas = @{
        "vscjava.vscode-java-pack"   = "Extension Pack for Java";
        "vmware.vscode-boot-dev-pack" = "Spring Boot Extension Pack"
    }
    foreach ($id in $requeridas.Keys) {
        if ($ext -contains $id) { Ok "$($requeridas[$id]) ($id)" }
        else { Falla "$($requeridas[$id]) no instalada ($id)" }
    }
} else { Falla "el comando 'code' no esta en el PATH (reinstala VS Code marcando 'Agregar a PATH')" }

Write-Host "`n3. Maven (dentro del proyecto)" -ForegroundColor Cyan
if (Test-Path ".\mvnw.cmd") {
    $mvn = & .\mvnw.cmd -v 2>&1 | Select-Object -First 1
    if ($LASTEXITCODE -eq 0) { Ok "Maven Wrapper: $mvn" } else { Falla "mvnw existe pero no se ejecuto correctamente" }
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    Ok ("Maven global: " + (& mvn -v 2>&1 | Select-Object -First 1))
} else {
    Info "no se encontro mvnw: ejecuta este script dentro de la carpeta del proyecto Spring Boot"
}

Write-Host "`n4. Proyecto Spring Boot" -ForegroundColor Cyan
if (Test-Path ".\pom.xml") {
    $pom = Get-Content .\pom.xml -Raw
    $deps = @{
        "spring-boot-starter-web"       = "Spring Web";
        "spring-boot-starter-thymeleaf" = "Thymeleaf";
        "spring-boot-starter-security"  = "Spring Security";
        "spring-boot-starter-data-jpa"  = "Spring Data JPA";
        "postgresql"                    = "PostgreSQL Driver";
        "spring-boot-starter-validation" = "Validation"
    }
    foreach ($d in $deps.Keys) {
        if ($pom -match [regex]::Escape($d)) { Ok "dependencia $($deps[$d])" } else { Falla "falta la dependencia $($deps[$d]) ($d)" }
    }
    if ($pom -match '<java.version>(\d+)</java.version>') { Ok "pom.xml usa Java $($Matches[1])" }
} else {
    Info "no hay pom.xml aqui: crea el proyecto o ejecuta el script dentro de su carpeta"
}

$props = ".\src\main\resources\application.properties"
if (Test-Path $props) {
    $p = Get-Content $props -Raw
    if ($p -match 'spring\.datasource\.url') { Ok "application.properties tiene la URL de la base de datos" } else { Falla "falta spring.datasource.url en application.properties" }
    if ($p -match 'ddl-auto\s*=\s*validate') { Ok "ddl-auto=validate configurado" } else { Info "falta spring.jpa.hibernate.ddl-auto=validate" }
}

Write-Host "`n5. Conexion a Supabase (opcional)" -ForegroundColor Cyan
if ($SupabaseHost) {
    $t = Test-NetConnection -ComputerName $SupabaseHost -Port $Puerto -WarningAction SilentlyContinue
    if ($t.TcpTestSucceeded) { Ok "se alcanza $SupabaseHost en el puerto $Puerto" }
    else { Falla "no se alcanza $SupabaseHost en el puerto $Puerto (revisa Internet, firewall o el host)" }
} else {
    Info "omitido: pasa -SupabaseHost con el host de Project Settings > Database"
}

Write-Host "`nResumen: $ok correctos, $fallas pendientes.`n" -ForegroundColor Cyan
