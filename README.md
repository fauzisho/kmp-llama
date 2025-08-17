# 🎥 KMP-Llama: SmolVLM Camera App

A **modern, cross-platform** camera AI analysis application built with **Kotlin Multiplatform** and **Compose Multiplatform**, featuring real-time SmolVLM integration for intelligent image understanding.

## ✨ Features

### 🎨 **Modern UI Design**
- **Dark theme with glassmorphism effects**
- **Gradient backgrounds and smooth animations**
- **Material 3 design system with custom theming**
- **Responsive layout with rounded corners and shadows**
- **Real-time loading indicators and progress feedback**

### 📱 **Cross-Platform Support**
- **Android** (API 24+) - Full CameraX integration with live preview
- **iOS** (x64, ARM64, Simulator ARM64) - AVFoundation ready (placeholder)
- **Desktop/JVM** - Webcam support ready (placeholder)

### 🤖 **AI Integration**
- **SmolVLM API integration** with Ktor HTTP client
- **Real-time image analysis** with configurable intervals
- **Type-safe JSON serialization** with Kotlinx Serialization
- **Intelligent error handling** and network resilience

### 📸 **Camera Features**
- **Live camera preview** with modern styling
- **Real-time frame capture** for analysis
- **Analysis overlay** with glassmorphism effects
- **Professional camera controls** and feedback

## 🚀 Quick Start

### Prerequisites
- Android Studio with KMP plugin
- Kotlin 2.2.0+
- Java 11+

### Clone and Run
```bash
git clone <repository-url>
cd kmp-llama

# Android
./gradlew :composeApp:installDebug

# Desktop
./gradlew :composeApp:run

# iOS
open iosApp/iosApp.xcodeproj
```

## 🏗️ Architecture

### 🔧 **Technology Stack**
```kotlin
// Core Framework
Kotlin Multiplatform 2.2.0
Compose Multiplatform 1.8.2

// UI & Design
Material 3 Design System
Material Icons Extended
Custom Dark Theme + Gradients

// Networking
Ktor 3.0.2 (Multiplatform HTTP)
Kotlinx Serialization 1.7.3

// Camera
CameraX 1.4.1 (Android)
AVFoundation (iOS - planned)
Webcam Libraries (Desktop - planned)
```

### 📦 **Module Structure**
```
composeApp/src/
├── commonMain/           # Shared business logic
│   ├── App.kt           # Modern UI with glassmorphism
│   ├── ApiService.kt    # Ktor-based SmolVLM client
│   └── Platform.kt      # Platform abstraction
├── androidMain/         # Android-specific
│   ├── CameraPreview.kt # CameraX live preview
│   ├── CameraCapture.kt # Real-time image capture
│   └── MainActivity.kt  # Permissions & lifecycle
├── iosMain/            # iOS-specific (ready)
└── jvmMain/           # Desktop-specific (ready)
```

## 🎨 UI Showcase

### **Modern Interface Features:**
- 🌌 **Gradient backgrounds** with depth and dimension
- 🔮 **Glassmorphism effects** on analysis overlays
- 📱 **Card-based layout** with elevation and shadows
- 🎯 **Smart typography** with proper hierarchy
- ⚡ **Smooth animations** and state transitions
- 🎨 **Professional color scheme** with accessibility

### **Smart Controls:**
- 🔧 **Server configuration** with validation
- 💬 **Dynamic question input** with multi-line support
- ⏱️ **Interval selection** with dropdown menu
- 🚀 **Start/Stop toggle** with visual feedback
- 📊 **Real-time response display** with scrolling

## 🔌 API Integration

### **SmolVLM Configuration**
```json
{
  "server_url": "http://192.168.0.244:8080",
  "endpoint": "/v1/chat/completions",
  "format": "OpenAI-compatible"
}
```

### **Request Format**
```kotlin
VisionRequest(
  model = "smolvlm",
  messages = [
    Message(
      role = "user",
      content = [
        Content(type = "text", text = "What do you see?"),
        Content(type = "image_url", 
               imageUrl = ImageUrl("data:image/jpeg;base64,..."))
      ]
    )
  ]
)
```

## 📱 Platform Implementation Status

| Platform | UI | Camera | API | Status |
|----------|----|---------|----|---------|
| **Android** | ✅ | ✅ CameraX | ✅ Ktor | **Complete** |
| **iOS** | ✅ | 🔄 AVFoundation | ✅ Ktor | **UI Ready** |
| **Desktop** | ✅ | 🔄 Webcam | ✅ Ktor | **UI Ready** |

## 🛠️ Development

### **Adding New Features**
1. **UI Changes**: Modify `commonMain/App.kt`
2. **Camera Features**: Update platform-specific `CameraPreview.kt`
3. **API Changes**: Edit `commonMain/ApiService.kt`
4. **Platform Code**: Add to respective `platformMain/` directories

### **Testing**
```bash
# Run tests
./gradlew :composeApp:testDebugUnitTest

# Check all platforms
./gradlew :composeApp:check
```

## 🎯 Roadmap

### **Immediate (v1.1)**
- [ ] iOS camera implementation with AVFoundation
- [ ] Desktop webcam integration
- [ ] Image gallery and history
- [ ] Offline model support

### **Future (v2.0)**
- [ ] Multi-model support (GPT-4V, Claude Vision)
- [ ] Voice commands and audio responses
- [ ] Real-time object tracking
- [ ] AR overlay integration
- [ ] Cloud sync and sharing

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **JetBrains** for Kotlin Multiplatform and Compose
- **SmolVLM** team for the vision language model
- **Material Design** for the beautiful design system
- **CameraX** team for robust camera APIs

---

**Built with ❤️ using Kotlin Multiplatform**
