# Wallapop Listing Assistant

Frontend de una sola pantalla inspirado en la página incluida en wallapop_web_sellMoment.zip.

## Frontend

Desde frontend/, ejecuta npm ci y npm run dev. Abre la dirección que muestra Vite.
Para verificar el código: npm run lint y npm run build. npm run preview sirve la compilación.

La cabecera, el menú lateral y las categorías son estáticos. El resumen admite hasta 50 caracteres, como la referencia. Continuar se habilita con texto distinto de espacios y muestra una confirmación local; editar el texto permite continuar de nuevo. No publica anuncios, no llama al backend y no requiere credenciales ni variables de entorno.

La paleta de src/styles/index.css usa los valores exactos del CSS aportado. Segoe UI, con alternativa Arial, sustituye a Wallie. El logo y el avatar proceden de la referencia. Los SVG similares son Font Awesome Free 6.7.2, del catálogo presentado en [W3Schools](https://www.w3schools.com/icons/fontawesome_icons_intro.asp), descargados del [repositorio oficial](https://github.com/FortAwesome/Font-Awesome/tree/6.x/svgs/solid) a src/assets/icons/. Su licencia y atribución están en esa carpeta.

Verificación manual: estado vacío, texto compuesto solo por espacios, límite de 50 caracteres, activación del botón, confirmación y edición posterior. Se revisa también la pantalla en escritorio y móvil. Estos casos cubren la única interacción local; no se añade un framework de pruebas para esta maqueta.

## Organización del frontend

La aplicación está integrada en frontend/ del repositorio. src/App.tsx compone la pantalla y src/main.tsx la monta.

Los componentes de UI están en src/components/: Header, CategoryNavigation, Sidebar, UserProfile, ListingCategories, ProductSummaryForm, SummaryField, Avatar e Icon. El estado del resumen y su confirmación permanece en ProductSummaryForm; SummaryField recibe el valor y el manejador de cambios mediante props. Icon solo admite nombres de SVG existentes mediante un tipo derivado de sus imports.

Los estilos se mantienen en src/styles/ y las imágenes y los SVG en src/assets/, siguiendo la estructura del proyecto. No se añaden dependencias ni cambia el comportamiento del formulario.

## Alcance pendiente

La conexión con el endpoint de sugerencias y sus estados de carga/error queda pendiente. Esta modificación no cambia la configuración ni los modos mock/real del backend. El tiempo invertido y la experiencia personal de uso de IA deben ser completados por el autor; AI_JOURNEY.md se conserva.
