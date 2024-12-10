import React from 'react'
// import ReactDOM from 'react-dom'
import * as ReactDomClient from 'react-dom/client'
import App from './components/App'
import './public/css/main.css'

const container  = document.getElementById('root')

const root = ReactDomClient.createRoot(container)

root.render(<App />)

// ReactDOM.render(
//   <App />,
//   root
// )

