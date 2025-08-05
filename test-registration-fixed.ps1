# Test Registration Endpoint with Fixed Code
$uri = "http://localhost:8080/api/auth/register"
$body = @{
    userId = "tokata"
    userNm = "tokata orl"
    userPwd = "123"
    eml = "tok@gmail.com"
    tel = "0987654321"
} | ConvertTo-Json

Write-Host "Testing registration endpoint..."
Write-Host "URI: $uri"
Write-Host "Body: $body"

try {
    $response = Invoke-RestMethod -Uri $uri -Method POST -ContentType "application/json" -Body $body
    Write-Host "Success! Response:"
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error occurred:"
    Write-Host "Exception Type: $($_.Exception.GetType().Name)"
    Write-Host "Exception Message: $($_.Exception.Message)"
    
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
        Write-Host "Status Description: $($_.Exception.Response.StatusDescription)"
        
        try {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "Response Body: $responseBody"
        } catch {
            Write-Host "Could not read response body: $($_.Exception.Message)"
        }
    }
} 