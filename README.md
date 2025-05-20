# To-Do App with Travel Planner

![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)
![GitHub issues](https://img.shields.io/github/issues/username/to-do-app.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/username/to-do-app.svg)
![GitHub stars](https://img.shields.io/github/stars/username/to-do-app.svg)

**To-Do App with Travel Planner** is a comprehensive Android application designed to streamline task management and travel planning. This app integrates real-time weather data via the Open-Meteo API, stunning destination images from the Unsplash API, and persistent local storage with Room Database. Built with modern Android development practices, it supports both USB and Wireless Debugging for testing on physical devices.

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage Guide](#usage-guide)
- [Project Architecture](#project-architecture)
- [Dependencies](#dependencies)
- [Development Setup](#development-setup)
- [Testing](#testing)
- [Known Issues](#known-issues)
- [Contributing Guidelines](#contributing-guidelines)
- [License](#license)
- [Acknowledgments](#acknowledgments)
- [Contact Information](#contact-information)

## Project Overview
This project was developed as a personal learning exercise and a practical tool for managing daily tasks and travel plans. It leverages Kotlin, Jetpack libraries, and third-party APIs to provide a seamless user experience. The app is compatible with Android 7.0 (API 24) and above, with optimizations for the latest Android 15 (API 35).

## Features
- **Task Management**: Create, edit, and delete tasks with a clean, intuitive UI.
- **Travel Planning**: Add detailed travel plans including destination, dates, budget, to-do lists, and places to visit.
- **Weather Integration**: Display real-time weather forecasts based on location and date using Open-Meteo API.
- **Image Loading**: Fetch and display high-quality images for destinations via Unsplash API.
- **Work Location Support**: Differentiate between travel and work locations with tailored UI adjustments.
- **Offline Functionality**: Store all data locally using Room Database.
- **Cross-Device Compatibility**: Supports physical devices via USB Debugging or Wireless Debugging (Android 11+).

## Screenshots
Explore the app's interface and functionality through these detailed screenshots, showcasing key features and screens:

| **Home Screen** - Displays the list of plans with action buttons. | **Add Travel Plan** - Form to input travel details. |
|------------------------------------------|--------------------------------------------|
| ![Home Screen](screenshots/home_screen.jpg) | ![Add Travel Plan](screenshots/add_travel_plan.png) |

| **Weather Display** - Shows weather forecast for the destination. | **Travel Plan Details** - Full view of a travel plan. |
|-------------------------------------------|-------------------------------------------|
| ![Weather Display](screenshots/weather_display.png) | ![Travel Plan Details](screenshots/travel_plan_details.png) |

| **Work Location View** - Customized view for work locations. | **Edit Plan Screen** - Interface for modifying plans. |
|-----------------------------------------|-----------------------------------------|
| ![Work Location View](screenshots/work_location_view.png) | ![Edit Plan Screen](screenshots/edit_plan_screen.png) |

*Note*: The `screenshots` folder is located in the root directory of the repository. Ensure all image files (`home_screen.png`, `add_travel_plan.png`, `weather_display.png`, `travel_plan_details.png`, `work_location_view.png`, `edit_plan_screen.png`) are uploaded to this folder.

## Requirements
### Development Environment
- **IDE**: Android Studio (latest stable version, e.g., Hedgehog 2023.1.1 or higher)
- **SDK**: Android SDK with API 35 (Android 15) installed
- **Build Tools**: Gradle 8.5 or higher
- **Java**: JDK 11 (embedded in Android Studio or manually installed)

### Hardware
- **Android Device**: Minimum API 24 (Android 7.0 Nougat), recommended API 35 (Android 15)
- **Operating System**: Windows 11 (tested), macOS, or Linux
- **Connectivity**: USB cable for USB Debugging or same Wi-Fi network for Wireless Debugging

### Dependencies
See [Dependencies](#dependencies) section for a complete list.

## Installation
Follow these steps to set up and run the project on your local machine:

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/username/to-do-app.git
   cd to-do-app
