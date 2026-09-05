import Header from './components/Header'
import ListingCategories from './components/ListingCategories'
import ProductSummaryForm from './components/ProductSummaryForm'
import Sidebar from './components/Sidebar'
import './styles/App.css'

function App() {
  return (
    <>
      <Header />
      <div className="page-layout">
        <Sidebar />
        <main>
          <div className="upload-page">
            <h1>Sube tu anuncio</h1>
            <ListingCategories />
            <ProductSummaryForm />
          </div>
        </main>
      </div>
    </>
  )
}

export default App
