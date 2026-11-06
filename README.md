# Media Server - Abstract Factory

Aplicación de escritorio desarrollada en Java con interfaz gráfica (Swing) capaz de buscar, descargar y reproducir contenido multimedia. El proyecto destaca por su arquitectura diseñada bajo el patrón de creación **Abstract Factory**.

## Características

* **Modo Vídeo:** Reproductor de vídeo integrado mediante JavaFX.
* **Modo Audio:** Reproductor de audio integrado utilizando `javax.sound.sampled`.
* **Archivos Locales:** Búsqueda y reproducción de archivos alojados en el directorio del proyecto (`media_files/`).
* **Integración con YouTube:** Búsqueda en tiempo real y descarga automática en segundo plano.

## Requisitos Previos

Para que el módulo de integración con YouTube funcione, es **estrictamente obligatorio** tener la herramienta de consola `yt-dlp` instalada y añadida al **PATH** del sistema.

* **Java JDK** (compatible con JavaFX).
* [**yt-dlp**](https://github.com/yt-dlp/yt-dlp) (instalado y accesible globalmente desde la terminal).

## Arquitectura

El núcleo del programa separa la lógica de las distintas familias multimedia (Audio vs. Vídeo) garantizando el principio de Inversión de Dependencias mediante el patrón **Abstract Factory**:

* `MediaFactory`: Factoría abstracta implementada por `AudioFactory` y `VideoFactory`.
* `MediaPlayer`: Interfaz común con los métodos de control de reproducción.
* `MediaSearcher`: Interfaz común para los algoritmos de búsqueda y selección (Locales y de YouTube).
* **Cliente:** La interfaz gráfica (`Main.java`) opera exclusivamente a través de estas interfaces abstractas, inyectando las dependencias necesarias en tiempo de ejecución.

## Ejecución

1. Clona este repositorio: `git clone https://github.com/DortaEspinosaPabloJose/DAP2627-Practica5`
2. Compila el proyecto asegurándote de incluir las librerías de JavaFX si tu JDK no las incluye por defecto.
3. Ejecuta la clase principal: `Main.java`.
4. El programa creará automáticamente las carpetas `media_files/video` y `media_files/audio` en la raíz del proyecto para gestionar las descargas.
