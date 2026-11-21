# ALL-IN-ONE

Aplicație Android nativă (Kotlin, Jetpack Compose, Material 3, Room, Media3)
care reunește 8 module într-un singur spațiu de lucru.

**Pachet:** `com.allinone.app`
**Min SDK / Target SDK:** 26 / 34

## Module

| Modul | Funcții principale |
|-------|--------------------|
| Spațiu (Infinite Space) | Spațiu 2.5D cu zoom, deplasare, tragere & plasare, foldere imbricate, previzualizare, meniuri la apăsare lungă, căutare globală, mod 2D grilă |
| Asistent AI | Chat, conversații, istoric, furnizori AI (compatibil OpenAI) |
| Cercetare AI | Căutare, documentare, salvarea rezultatelor |
| Email | Trimitere, primire, citire, organizare pe foldere, atașamente |
| Scanner | CameraX, OCR (ML Kit), export PDF |
| Video | Redare locală (Media3), bibliotecă, liste de redare |
| Productivitate | Note, sarcini, calendar, mementouri |
| Fișiere | Acces SAF, organizare, partajare |
| Setări | Personalizare, securitate (Keystore), conexiuni, backup |

## Securitate
- Cheile API și parolele sunt stocate în `EncryptedSharedPreferences`
  (Android Keystore), nu în cod sau în repository.
- Fără secrete în codul sursă.

## Construire

```bash
# cu wrapper (după generare) sau cu Gradle instalat:
gradle :app:assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## CI
`.github/workflows/android.yml` compilează APK-urile debug/release și le
publică ca artifact descărcabil.

## Teste
```bash
gradle :app:testDebugUnitTest
```
