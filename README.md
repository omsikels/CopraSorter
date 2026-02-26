<p align="center">
<h1 align="center">🥥 CopraSorter: AI-Powered Coconut Quality Grading</h1>
</p>

<p align="center">
<img src="https://img.shields.io/badge/Python-3.9+-3776AB?style=for-the-badge&logo=python&logoColor=white" alt="Python">
<img src="https://img.shields.io/badge/TensorFlow-2.x-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white" alt="TensorFlow">
<img src="https://img.shields.io/badge/OpenCV-4.x-5C3EE8?style=for-the-badge&logo=opencv&logoColor=white" alt="OpenCV">
<img src="https://img.shields.io/badge/Flask-Enabled-000000?style=for-the-badge&logo=flask&logoColor=white" alt="Flask">
<img src="https://img.shields.io/badge/Arduino-Prototyping-00979D?style=for-the-badge&logo=arduino&logoColor=white" alt="Arduino">
</p>

> **CopraSorter** is an automated agricultural solution designed to modernize the coconut industry. By combining Computer Vision with Deep Learning, this platform identifies, classifies, and sorts copra (dried coconut meat) based on quality standards.

---

## 🌟 What Does This Platform Do?

| Role | Description |
| --- | --- |
| 🔍 **Visual Analysis** | The system captures high-resolution images of copra samples. Using a trained CNN model, it detects defects like mold, discoloration, or moisture damage. |
| 🏗️ **Automated Sorting** | Once classified, the system interfaces with hardware (via Arduino/Serial) to physically sort the copra into different bins. |

---

## 🤖 Machine Learning Architecture

To achieve high accuracy in agricultural environments, CopraSorter utilizes a specialized **Convolutional Neural Network (CNN)** optimized for texture and color feature extraction.



[Image of Convolutional Neural Network architecture for image classification]


### The 4-Stage Classification Pipeline

| Stage | Process | Technology Used |
| --- | --- | --- |
| **1** | **Image Acquisition:** Captures raw frames from the sorting conveyor camera. | OpenCV / PiCam |
| **2** | **Preprocessing:** Resizes, normalizes, and applies Gaussian blurring. | NumPy / OpenCV |
| **3** | **Feature Inference:** Model analyzes the surface for mold and color consistency. | TensorFlow / Keras |
| **4** | **Hardware Trigger:** Sends a signal to the actuator based on the class. | PySerial / Arduino |

**Grading Categories:**

> `🏆 Grade A (Premium)` | `🥈 Grade B (Standard)` | `❌ Reject (Moldy/Damaged)`

---

## 🚀 Getting Started (How to Run)

To ensure the system communicates correctly with both the camera and the sorting hardware, follow these steps:

### Prerequisites
* **Python 3.9+**
* **USB Camera** or Raspberry Pi Camera Module.
* **Arduino/ESP32** (If using the physical sorting component).

### Installation & Launch

1. **Clone the Repository:**
   ```bash
   git clone [https://github.com/omsikels/CopraSorter.git](https://github.com/omsikels/CopraSorter.git)
   cd CopraSorter

Install Dependencies:

Bash
pip install -r requirements.txt
Run the Application:

For the web-based dashboard:

Bash
python app.py
For the standalone sorting script:

Bash
python main_sorter.py

📁 Advanced Info: Directory Structure
<details>
<summary><b>🖱️ Click to expand the folder architecture</b></summary>

Below is the visual map of the CopraSorter project structure:

📦 CopraSorter
 ┣ 📂 models/             # 🧠 Pre-trained .h5 or .tflite model files
 ┣ 📂 datasets/           # 🖼️ Training and validation image sets
 ┣ 📂 static/             # 🎨 CSS and Frontend assets for the UI
 ┣ 📂 templates/          # 📄 HTML files for the Flask dashboard
 ┣ 📂 hardware/           # 🔌 Arduino (.ino) source code for actuators
 ┣ 📜 app.py              # ⚙️ Main Flask Web Server
 ┣ 📜 classifier.py       # 🧪 Logic for ML Image Prediction
 ┗ 📜 requirements.txt    # 📋 List of required Python libraries

Component DetailsFile
File / Folder,Purpose
📁 /models/,Contains the trained weights that allow the AI to recognize copra quality.
📁 /hardware/,Contains the firmware that controls physical motors or servos.
📄 app.py,The main entry point that hosts the monitoring dashboard.
📄 classifier.py,Handles OpenCV image processing and TensorFlow model inference.
</details>

🔐 System Access
Once the Flask server is running, you can access the interface at:

📊 Real-time Monitor: http://localhost:5000

🛠️ Admin Settings: http://localhost:5000/settings
