# pandero
Pandero audio player - native Anroid audio player written in Kotlin

Tutorial: https://www.youtube.com/watch?v=HsSIgjraJq0&list=PL0pXjGnY7POQCLkvT6jRpCDGnBZLe2HGW&index=2

# Initial steps

## Project
- create an empty activity

## Splashscreen
- Under `Gradle Scripts` / build.gradle.kts (Module :app) add
   - dependencies { implementation("androidx.core:core-splashscreen:1.0.1") }
   - replace with `implementation(libs.androidx.core.splashscreen)` via IDE completion
   - sync now to update references

## Logo
- Under /res/drawable rightclick, new, Vector asset, local file
- load svg and set it to "ic_logo" with 50px/50px


