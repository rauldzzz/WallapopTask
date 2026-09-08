# Wallapop Listing Assistant

Frontend de una sola pantalla inspirado en la página incluida en wallapop_web_sellMoment.zip.

## Frontend

Desde frontend/, ejecuta npm ci y npm run dev. Abre la dirección que muestra Vite.
Para verificar el código: npm run lint y npm run build. npm run preview sirve la compilación.

La cabecera, el menú lateral y las categorías son estáticos. El resumen admite hasta 50 caracteres, como la referencia. Continuar envía la descripción al backend y muestra título, etiquetas y rango de precios. Requiere al menos 3 caracteres después de recortar espacios. Incluye carga, errores y reintento; editar cancela la petición anterior y limpia el resultado. No publica anuncios. Las credenciales permanecen en el backend.

La paleta de src/styles/index.css usa los valores exactos del CSS aportado. Segoe UI, con alternativa Arial, sustituye a Wallie. El logo y el avatar proceden de la referencia. Los SVG similares son Font Awesome Free 6.7.2, del catálogo presentado en [W3Schools](https://www.w3schools.com/icons/fontawesome_icons_intro.asp), descargados del [repositorio oficial](https://github.com/FortAwesome/Font-Awesome/tree/6.x/svgs/solid) a src/assets/icons/. Su licencia y atribución están en esa carpeta.

Verificación manual: estado vacío, texto compuesto solo por espacios, límite de 50 caracteres, activación del botón, confirmación y edición posterior. Se revisa también la pantalla en escritorio y móvil. Las pruebas automatizadas del cliente REST se describen en la sección Flujo REST local.

## Organización del frontend

La aplicación está integrada en frontend/ del repositorio. src/App.tsx compone la pantalla y src/main.tsx la monta.

Los componentes de UI están en src/components/: Header, CategoryNavigation, Sidebar, UserProfile, ListingCategories, ProductSummaryForm, ListingSuggestion, SummaryField, Avatar e Icon. El hook src/hooks/useProductSummary.ts gestiona el estado, la validación, el envío y los estados de la sugerencia; ProductSummaryForm compone la interfaz y conecta sus eventos al hook; SummaryField recibe el valor y el manejador de cambios mediante props. Icon solo admite nombres de SVG existentes mediante un tipo derivado de sus imports.

Los estilos se mantienen en src/styles/ y las imágenes y los SVG en src/assets/, siguiendo la estructura del proyecto. Los contratos TypeScript están en src/model/listing.ts: SuggestionRequest contiene description; ListingRequest refleja la respuesta del backend con title, tags y priceRange (min y max numéricos). useProductSummary mantiene un SuggestionRequest actualizado desde SummaryField y recorta espacios al continuar. src/api/listings.ts centraliza el POST y valida el JSON recibido en ejecución antes de mostrarlo. ListingSuggestion presenta el resultado. No se añaden dependencias.

## Backend: Spring AI

Base: commit 3afc793, anterior a las integraciones Vertex AI y REST manual. Se mantiene el frontend y el contrato de ListingRequest: title, tags y priceRange (min y max de tipo BigDecimal).

Versiones: Spring Boot 3.5.8, Spring AI 1.1.2, Kotlin 1.9.25 y Java 21. Boot se actualiza para usar la versión compatible con este Spring AI; las dependencias se resuelven desde Maven Central. El starter es spring-ai-starter-model-google-genai.

Estructura bajo backend/src/main/kotlin/com/wallapoptest/listing_assistant/:

- controller/: endpoint y configuración estricta del JSON de entrada.
- service/: conversión con BeanOutputConverter de Spring AI y validación de la sugerencia.
- model/: DTO de entrada SuggestionRequest y resultado ListingRequest con PriceRange.
- aiassistant/: interfaz, implementación Gemini con ChatClient, mock, propiedades y configuración de clientes.
- error/: excepciones y respuestas HTTP seguras.

La llamada real utiliza Spring AI. No construimos peticiones HTTP ni extraemos manualmente el JSON de respuesta de la API de Google. La conversión y validación son comunes al modo real y al mock.

### Ejecutar

Desde backend/:

```powershell
.\gradlew.bat bootRun
```

En Unix: ./gradlew bootRun. Arranca en http://localhost:8080. Por defecto MOCK_MODE=true; no necesita credenciales ni inicializa clientes de Gemini.

### IntelliJ y dependencias

Abre o vincula backend/build.gradle.kts como proyecto Gradle. Configura Gradle JVM con JDK 21 y usa el wrapper del proyecto; después pulsa Reload All Gradle Projects en la ventana Gradle. Si el IDE sigue mostrando Spring Boot 3.3.4, su modelo no coincide con el build actual (3.5.8).

El starter trae spring-ai-google-genai 1.1.2 y com.google.genai:google-genai 1.28.0, que contienen los imports GoogleGenAiChatModel, GoogleGenAiChatOptions, Client, HttpOptions, HttpRetryOptions y ApiException. No hace falta añadir el antiguo starter de Vertex. Para verificar resolución y compilación desde backend/:

```powershell
.\gradlew.bat dependencyInsight --dependency com.google.genai:google-genai --configuration compileClasspath
.\gradlew.bat clean test bootJar
```

### Endpoint

POST /api/listings/suggestions:

```json
{"description":"Chaqueta de cuero vintage, usada una vez, talla M"}
```

Respuesta mock:

```json
{
  "title":"Chaqueta de cuero vintage talla M, usada una vez",
  "tags":["chaqueta","cuero","vintage","talla M"],
  "priceRange":{"min":40.00,"max":50.00}
}
```

El mock devuelve un ejemplo guardado, independientemente del producto descrito. El rango es una estimación en EUR.

Se validan descripciones de 3–2000 caracteres, títulos de 3–120, 3–5 tags distintos de 1–30 caracteres y ambos extremos del rango de 0.01 a 99999999.99 con máximo dos decimales y min <= max. Se rechaza JSON inválido, campos extra, valores nulos, tipos incorrectos y respuestas mayores de 8192 caracteres. La validación no garantiza la exactitud de una valoración de mercado.

### Variables

| Variable | Uso |
| --- | --- |
| MOCK_MODE | true por defecto; false activa Gemini. |
| MOCK_SCENARIO | VALID, MALFORMED o NONSENSICAL; por defecto VALID. |
| GEMINI_API_KEY | API key de Google AI Studio; obligatoria y no vacía en modo real. |
| GEMINI_MODEL | Por defecto gemini-3.5-flash-lite. |
| GEMINI_TIMEOUT_SECONDS | Límite total de llamada, incluyendo conexión y respuesta; 20 segundos por defecto, entre 1 y 60. |

Para probar errores, establece MOCK_SCENARIO=MALFORMED (JSON roto) o NONSENSICAL (contenido incoherente) y reinicia el servidor. Ambos deben devolver 502/INVALID_MODEL_RESPONSE.

Para Gemini real, configura MOCK_MODE=false y GEMINI_API_KEY en las variables de entorno de la configuración de ejecución de IntelliJ. En PowerShell puedes introducir la clave sin guardarla en el historial:

```powershell
$env:MOCK_MODE = "false"
$secureKey = Read-Host "GEMINI_API_KEY" -AsSecureString
$env:GEMINI_API_KEY = [System.Net.NetworkCredential]::new("", $secureKey).Password
.\gradlew.bat bootRun
```

No hacen falta proyecto, región ni archivos de credenciales de Vertex. No se cargan archivos .env automáticamente. Nunca guardes la clave en Git ni uses variables VITE_ para ella.

[Google GenAI de Spring AI](https://docs.spring.io/spring-ai/reference/1.1/api/chat/google-genai-chat.html) admite API key de [Google AI Studio](https://aistudio.google.com/apikey). Los modelos con cuota gratuita están sujetos a los [límites y precios del proveedor](https://ai.google.dev/gemini-api/docs/pricing).

AssistantConfiguration crea clientes solo en modo real. Se deshabilitan las autoconfiguraciones de chat/embeddings que no usamos para evitar inicializar Google en mock. No se habilitan herramientas ni reintentos automáticos. BeanOutputConverter tiene su logger desactivado porque puede registrar contenido bruto cuando falla la conversión.

### Errores y pruebas

Errores con formato {"code":"...","message":"..."}: 400 para entrada inválida, 502 para salida inválida del modelo, 503 para fallo del proveedor y 504 para timeout. No se devuelve el error bruto de Google.

Ejecuta .\gradlew.bat test o ./gradlew test desde backend/. Las pruebas cubren el contrato HTTP, conversión y validación, mocks rotos, separación entre descripción e instrucciones, errores del proveedor y arranque sin clave. Gemini se simula con ChatModel: no se hacen llamadas reales ni se necesitan credenciales para los tests.

## Pendiente

Comprobar el flujo con credenciales propias y revisar la calidad de las sugerencias reales. Esta integración no realiza una llamada real ni modifica AI_JOURNEY.md.

Tiempo invertido: pendiente de completar por el autor.
Siguiente paso: revisar la calidad de las sugerencias y preparar el despliegue.

## Flujo REST local

1. Desde backend/, ejecuta `.\gradlew.bat bootRun` (mock sin clave por defecto).
2. Desde frontend/, ejecuta `npm ci` y `npm run dev`.
3. Abre la dirección de Vite, escribe una descripción y pulsa Continuar.

Vite reenvía `/api` a `http://localhost:8080`, tanto en desarrollo como en `npm run preview`. Reinicia Vite si ya estaba abierto antes del cambio de configuración. El navegador utiliza el mismo origen y no necesita CORS. Un despliegue debe configurar su propio proxy `/api` al backend: el proxy de Vite no forma parte de los archivos estáticos compilados.

Para probar errores de modelo, reinicia el backend con `MOCK_SCENARIO=MALFORMED` o `NONSENSICAL`; la pantalla mostrará el error y permitirá reintentar. Para modo real utiliza `MOCK_MODE=false` y `GEMINI_API_KEY` exclusivamente en el proceso backend.

`npm test` usa el runner integrado de Node (Node 22.18+ o 24) sin dependencias nuevas. Cubre contrato POST, validación de datos, respuesta vacía/JSON roto y errores HTTP/red porque son las fronteras de la integración. `npm run lint` y `npm run build` verifican el código y la compilación. Revisar en navegador carga, edición durante una petición, reintento y presentación de resultados.
