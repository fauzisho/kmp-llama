# 🎥 KMP-Llama: SmolVLM Camera App
This repository is a simple demo for how to use llama.cpp server and mobile application with SmolVLM 500M to get real-time object detection

<div align="center">
  <img src="demo.gif" alt="KMP-Llama Demo" width="300"/>
  <img src="demo2.gif" alt="KMP-Llama Demo 2" width="300"/>
</div>

## How to setup
1. Install [llama.cpp](https://github.com/ggml-org/llama.cpp)
2. Run ` llama-server -hf ggml-org/SmolVLM-500M-Instruct-GGUF --host 0.0.0.0 --port 8080`  
   Note: you may need to add `-ngl 99` to enable GPU (if you are using NVidia/AMD/Intel GPU)  
   Note (2): You can also try other models [here](https://github.com/ggml-org/llama.cpp/blob/master/docs/multimodal.md)
3. Run ` ifconfig | grep "inet" `to get the LAN (Wi-Fi) address
   Example: inet 127.0.0.1 netmask 0xff000000
   inet 192.168.0.244 netmask 0xffffff00 broadcast 192.168.0.255
5. Run KMP App project (eg. Android)
6. Optionally change the instruction (for example, make it returns JSON)
7. Click on "Start" and enjoy

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
---


Please use this bibtex if you want to cite this repository in your publications:

    @misc{kmpllama,
       author = {Sholichin, Fauzi},
       title = {KMP-Llama: SmolVLM Camera App},
       year = {2025},
       publisher = {GitHub},
       journal = {GitHub repository},
       howpublished = {\url{https://github.com/fauzisho/kmp-llama}},
      }
      
**Built with ❤️ using Kotlin Multiplatform**
