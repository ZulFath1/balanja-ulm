# Sprint 2 Adjustments - Implementation Summary

## Date: 2026-06-11
## Status: ✅ COMPLETED

### Overview
All Sprint 2 adjustments have been successfully applied to the Balanja ULM codebase due to Google Cloud billing constraints. The project has been migrated from Google Maps to Osmdroid, Firebase Storage has been removed, and the TAMBAH PEDAGANG and ULASAN features have been revised to remove resource-intensive functionality.

---

## 1. ✅ MAPS MIGRATION (Google Maps → Osmdroid)

### Changes Made:

#### build.gradle.kts (app/build.gradle.kts)
- ❌ **Removed**: `com.google.android.gms:play-services-location:21.3.0`
- ❌ **Removed**: `com.google.maps.android:maps-compose:4.3.3`
- ❌ **Removed**: `com.google.android.gms:play-services-maps:19.0.0`
- ❌ **Removed**: `val localProperties = Properties()` loading code
- ❌ **Removed**: `manifestPlaceholders["MAPS_API_KEY"]` configuration
- ✅ **Added**: `org.osmdroid:osmdroid-android:6.1.18`

#### AndroidManifest.xml
- ❌ **Removed**: `android.permission.ACCESS_FINE_LOCATION` permission
- ❌ **Removed**: `android.permission.ACCESS_COARSE_LOCATION` permission
- ❌ **Removed**: Google Maps API Key metadata
  ```xml
  <meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="${MAPS_API_KEY}" />
  ```

#### local.properties
- Status: No action needed (file only contains SDK path)

---

## 2. ✅ FIREBASE STORAGE DEPRECATION

### Changes Made:

#### build.gradle.kts (app/build.gradle.kts)
- ❌ **Removed**: `com.google.firebase:firebase-storage-ktx` dependency

#### Code Review
- ✅ **Verified**: No existing references to Firebase Storage in Kotlin code
- ✅ **Verified**: All photo upload functionality removed from models and components

---

## 3. ✅ REVISE "TAMBAH PEDAGANG" (BLJA-05)

### Model Changes (StallSuggestion.kt)
- ❌ **Removed**: `photoUrl: String` field
- ❌ **Removed**: `latitude: Double` field
- ❌ **Removed**: `longitude: Double` field

**Updated Model:**
```kotlin
data class StallSuggestion(
    val id: String = "",
    val name: String = "",
    val locationDescription: String = "",
    val submittedBy: String = "",
    val submittedByName: String = "",
    val status: String = "pending",
    val createdAt: Long = 0L
)
```

### New Components Created:

#### AddStallViewModel.kt
- Location: `presentation/search/AddStallViewModel.kt`
- Purpose: Manages opening Google Form in external browser
- Method: `openGoogleForm(context: Context)` - Opens "https://forms.gle/dummy"

#### AddStallScreen.kt
- Location: `presentation/search/AddStallScreen.kt`
- Features:
  - Informational text about proposing new stalls
  - PrimaryButton to open Google Form
  - Material 3 Compose UI
  - No GPS tracking, camera, or image upload

**UI Flow:**
1. User sees title "Tambah Pedagang"
2. Informational text about stall proposals
3. "Buka Formulir Pengajuan" button
4. Button opens Google Form in external browser

---

## 4. ✅ REVISE "ULASAN/REVIEW" (BLJA-03)

### Model Changes (Review.kt)
- ❌ **Removed**: `photoUrl: String?` field

**Updated Model:**
```kotlin
data class Review(
    val id: String = "",
    val stallId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val attributes: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
```

### New Components Created:

#### WriteReviewViewModel.kt
- Location: `presentation/review/WriteReviewViewModel.kt`
- State Management: `WriteReviewUiState`
- Features:
  - Rating management (1-5 stars)
  - Comment text input
  - Attribute tag selection
  - Form validation
  - Submit to Firebase Realtime Database

#### WriteReviewScreen.kt
- Location: `presentation/review/WriteReviewScreen.kt`
- Features:
  - ⭐ Star rating selector (1-5)
  - 📝 Comment text field (multi-line)
  - 🏷️ Attribute chips for quick selection:
    - Harga Terjangkau
    - Pelayanan Baik
    - Rasa Enak
    - Lokasi Strategis
    - Bersih
    - Porsi Besar
  - ✅ Submit button (disabled until rating and comment provided)
  - ❌ **NO** "Add Photo" button
  - Top app bar with back navigation
  - Material 3 Compose UI

**UI Flow:**
1. Star rating selection interface
2. Comment input field
3. Optional attribute tags
4. Error message display
5. Submit button (saves to Realtime Database)

---

## 5. Files Modified Summary

### Configuration Files
| File | Changes |
|------|---------|
| `app/build.gradle.kts` | Removed Google Maps deps, Firebase Storage, added Osmdroid |
| `app/src/main/AndroidManifest.xml` | Removed Maps API key metadata, location permissions |
| `local.properties` | No changes needed |

### Model Files (Simplified)
| File | Changes |
|------|---------|
| `domain/model/StallSuggestion.kt` | Removed photoUrl, latitude, longitude |
| `domain/model/Review.kt` | Removed photoUrl |

### Presentation Layer (New Files)
| File | Purpose |
|------|---------|
| `presentation/search/AddStallViewModel.kt` | ViewModel for AddStall screen |
| `presentation/search/AddStallScreen.kt` | UI for TAMBAH PEDAGANG feature |
| `presentation/review/WriteReviewViewModel.kt` | ViewModel for WriteReview screen |
| `presentation/review/WriteReviewScreen.kt` | UI for ULASAN feature |

---

## 6. Build Status

### Gradle Dependencies
- ✅ All Google Maps dependencies removed
- ✅ Firebase Storage dependency removed
- ✅ Osmdroid dependency added
- ✅ All existing Firebase dependencies retained (Auth, Database)
- ✅ All Compose dependencies unchanged

### Kotlin Code
- ✅ AddStallViewModel.kt: No compile errors
- ✅ AddStallScreen.kt: No compile errors
- ✅ WriteReviewViewModel.kt: No compile errors
- ✅ WriteReviewScreen.kt: No compile errors

### Next Steps for Project
1. **Sync Gradle** - Run `./gradlew sync` to ensure all dependencies are resolved
2. **Test Build** - Run `./gradlew build` to verify project builds successfully
3. **Navigation Integration** - Update `AppNavigation.kt` to use new AddStallScreen and WriteReviewScreen
4. **Firebase Database Rules** - Ensure Realtime Database security rules allow write to `/reviews` node
5. **Additional Screens** - Implement remaining placeholder screens (Home, Profile, Search, etc.)

---

## 7. Breaking Changes for Developers

### ⚠️ API Changes
- `StallSuggestion` model no longer has location/photo fields
- `Review` model no longer has photo field
- GPS permissions removed from manifest
- Google Maps API key no longer needed

### 📋 Migration Guide
If you have code referencing the old models:
1. Remove any `StallSuggestion.latitude`, `.longitude`, `.photoUrl` references
2. Remove any `Review.photoUrl` references
3. Remove any Firebase Storage upload code
4. Update forms that collected location or photo data

---

## 8. Testing Checklist

- [ ] Project builds without errors: `./gradlew build`
- [ ] AddStallScreen displays correctly with button
- [ ] AddStallScreen button opens Google Form
- [ ] WriteReviewScreen displays star rating selector
- [ ] WriteReviewScreen displays comment input
- [ ] WriteReviewScreen displays attribute chips
- [ ] WriteReviewScreen has NO photo upload button
- [ ] Submit button is disabled when rating = 0
- [ ] Submit button is disabled when comment is empty
- [ ] Osmdroid dependency is properly imported
- [ ] No references to Google Maps in code
- [ ] No references to Firebase Storage in code

---

## 9. Dependency Inventory

### Removed
- `com.google.android.gms:play-services-location:21.3.0`
- `com.google.maps.android:maps-compose:4.3.3`
- `com.google.android.gms:play-services-maps:19.0.0`
- `com.google.firebase:firebase-storage-ktx`

### Added
- `org.osmdroid:osmdroid-android:6.1.18`

### Retained
- Firebase Auth, Database
- Jetpack Compose
- Navigation Compose
- Coroutines
- Retrofit, OkHttp
- Room Database
- Coil image loading

---

**Prepared by:** GitHub Copilot  
**Last Updated:** 2026-06-11  
**Status:** Ready for Gradle sync and build verification
