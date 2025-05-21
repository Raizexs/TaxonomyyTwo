Sistema de Evaluación Basado en la Taxonomía de Bloom (Java GUI)

Este proyecto fue desarrollado como parte de la asignatura Paradigmas de Programación, correspondiente al tercer año de la carrera de Ingeniería en Computación e Informática.

El objetivo principal es construir una aplicación de escritorio con interfaz gráfica (GUI) en Java que permita la aplicación y gestión de pruebas compuestas por ítems clasificados según los niveles de la Taxonomía de Bloom. El sistema está diseñado para facilitar la administración de evaluaciones y la revisión de respuestas, siguiendo principios de programación orientada a objetos y un patrón de diseño que favorece la modularidad.
Objetivos del Proyecto

    Cargar descripciones de ítems para una prueba desde un archivo externo, mostrando la cantidad total de ítems y el tiempo estimado de la prueba.

    Aplicar la prueba mostrando un ítem a la vez, permitiendo al usuario responder y navegar entre preguntas, manteniendo las respuestas ingresadas.

    Revisar las respuestas una vez finalizada la prueba, proporcionando un resumen detallado del rendimiento y la posibilidad de revisar cada ítem individualmente.

    Validar y administrar formatos de archivo incorrectos o incompletos mediante el uso de excepciones.

Tecnologías Utilizadas

    Lenguaje: Java

    Interfaz Gráfica (GUI): Java Swing (utilizando GroupLayout para la disposición de los elementos).

    Entorno de desarrollo: IntelliJ IDEA (recomendado).

    Plataforma de ejecución: Java Virtual Machine (JVM).

Funcionalidades Clave

El sistema implementa las siguientes funcionalidades principales:

    Carga de Ítems desde Archivo:

        Al iniciar el programa, se permite al usuario seleccionar un archivo de su almacenamiento local que contiene la descripción de los ítems de la prueba.

        Una vez que el archivo es cargado correctamente, la interfaz gráfica despliega la cantidad total de ítems y el tiempo total estimado para completar la prueba.

        Se habilita un botón que permite "Iniciar prueba".

        El formato del archivo es definido por los estudiantes (ver sección "Formato del Archivo de Ítems"). Cualquier error o formato incompleto es validado y gestionado mediante excepciones.

    Aplicación de la Prueba:

        La prueba se presenta en una ventana donde se muestra un ítem por vez, junto con sus posibles opciones de respuesta.

        Se disponen las siguientes acciones de navegación:

            "Volver atrás": Permite retroceder al ítem anterior. Esta opción se deshabilita si el usuario se encuentra en la primera pregunta.

            "Avanzar a la siguiente": Permite avanzar al siguiente ítem. Cuando el usuario está en la última pregunta, la funcionalidad de este botón cambia a "Enviar respuestas", lo que procede a la revisión de las mismas.

        Las respuestas ingresadas por el usuario se mantienen al moverse entre ítems.

    Revisión de Respuestas:

        Una vez finalizada la prueba y enviadas las respuestas, el sistema presenta un resumen visual que incluye:

            Porcentaje de respuestas correctas desglosadas según el nivel de la Taxonomía de Bloom al que pertenecen los ítems.

            Porcentaje de respuestas correctas desglosadas según el tipo de ítem.

        Se incluye un botón que permite "Revisar respuestas". Al activarlo, la visualización es equivalente a la de la fase de aplicación de la prueba, pero indicando si la respuesta del usuario fue correcta o incorrecta.

        Durante la revisión, se mantienen las acciones de "Volver atrás" y "Avanzar a la siguiente", con la misma funcionalidad que en la aplicación de la prueba.

        Adicionalmente, se añade un botón para "Volver al resumen" de respuestas correctas.

Alcances y Restricciones

    Modularización: El desarrollo está estructurado en dos paquetes principales:

        backend: Contiene la lógica de negocio de la aplicación (gestión de ítems, evaluación, etc.).

        frontend: Gestiona la Interfaz Gráfica de Usuario (GUI).

        La comunicación entre estos paquetes se realiza mediante un patrón de notificación-suscripción (Observer), donde los eventos de la GUI generan mensajes al backend, y las actualizaciones de este último son notificadas de manera asincrónica al frontend.

    Formato del Archivo de Ítems: El formato del archivo con los ítems es a criterio de los estudiantes. El sistema está diseñado para validar y administrar formatos erróneos o incompletos, por ejemplo, mediante el uso de excepciones (IOException u otras excepciones personalizadas).

    Tipos de Ítems: Los tipos de ítems soportados son: selección múltiple y verdadero/falso.

    Archivo Léame: Este archivo (README.md) cumple con el requisito de incluir todos los alcances, supuestos incorporados e instrucciones para su ejecución, incluyendo la descripción del formato de archivo de ítems.

    Layout de la GUI: Se ha optado por utilizar GroupLayout para la disposición de los elementos en la interfaz gráfica, buscando un diseño flexible y adaptable.

Formato del Archivo de Ítems

El archivo de ítems es un archivo de texto plano (.txt) donde cada línea representa un ítem de la prueba. Los campos de cada ítem están separados por un punto y coma (;).

Estructura de cada línea:

Tipo;Nivel_Bloom;Enunciado;Opciones|Separadas|Por|Barra;Respuesta_Correcta;Tiempo_Estimado_Segundos

Ejemplo de línea para Selección Múltiple:

SELECCION_MULTIPLE;RECORDAR;¿Cuál es la capital de Francia?;París|Londres|Madrid|Roma;París;30

Ejemplo de línea para Verdadero/Falso:

VERDADERO_FALSO;ENTENDER;El sol gira alrededor de la Tierra.;Verdadero|Falso;Falso;15

Consideraciones:

    Tipo: Puede ser SELECCION_MULTIPLE o VERDADERO_FALSO.

    Nivel_Bloom: Un string que representa el nivel de la taxonomía de Bloom (ej. RECORDAR, ENTENDER, APLICAR, ANALIZAR, EVALUAR, CREAR).

    Enunciado: El texto de la pregunta.

    Opciones: Para SELECCION_MULTIPLE, las opciones se separan por una barra vertical (|). Para VERDADERO_FALSO, las opciones siempre serán Verdadero|Falso.

    Respuesta_Correcta: La opción correcta tal como aparece en las opciones.

    Tiempo_Estimado_Segundos: El tiempo en segundos estimado para responder el ítem.

Compilación y Ejecución

Para compilar y ejecutar este proyecto, sigue estos pasos:

    Clonar el repositorio:

    git clone https://github.com/tu-usuario/nombre-del-repositorio.git
    cd nombre-del-repositorio

    Abrir en IntelliJ IDEA:

        Abre IntelliJ IDEA.

        Selecciona "Open" (Abrir) y navega hasta la carpeta raíz del proyecto que acabas de clonar.

    Ejecutar el proyecto:

        Localiza el archivo Main.java (normalmente en src/cl/unab/inf/sistemaevaluacion/Main.java).

        Haz clic derecho sobre Main.java y selecciona "Run 'Main.main()'".

        Alternativamente, puedes usar el botón de "Run" (ejecutar) en la barra de herramientas de IntelliJ IDEA.

    Cargar el archivo de ítems:

        Una vez que la aplicación se inicie, verás la interfaz principal.

        Haz clic en el botón "Cargar archivo de ítems" y selecciona el archivo de texto (.txt) que contiene tus ítems (siguiendo el formato descrito anteriormente).

Observaciones Finales

Este sistema fue diseñado para ser una aplicación de escritorio robusta y funcional, cumpliendo con los requerimientos académicos de la asignatura. Su modularidad y el uso de patrones de diseño facilitan futuras extensiones, como la persistencia de datos en bases de datos o la integración con servicios web.
Licencia y Uso

Este proyecto fue creado con fines educativos y puede ser utilizado libremente como referencia para trabajos académicos o aprendizaje personal.
Autores

Proyecto desarrollado por estudiantes de tercer año de Ingeniería en Informática.
Colaboradores: Lukas Flores (@Raizexs), David Vásquez.
