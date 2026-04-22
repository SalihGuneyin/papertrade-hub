import { startTransition, useDeferredValue, useEffect, useState } from 'react'
import './App.css'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

const watchlistStatuses = ['ACTIVE', 'READY', 'PAUSED']
const tradeSides = ['BUY', 'SELL']
const trackerFilters = ['ALL', ...watchlistStatuses]

const initialAssetForm = {
  symbol: '',
  name: '',
  assetClass: 'Crypto',
  currentPrice: 0,
  dailyChangePercent: 0,
  thesis: '',
  active: true,
}

const initialWatchlistForm = {
  assetId: '',
  status: 'ACTIVE',
  targetPrice: 0,
  convictionScore: 70,
  setupNotes: '',
}

const initialTradeForm = {
  assetId: '',
  side: 'BUY',
  quantity: 0.1,
  executionPrice: 0,
  strategyTag: '',
  tradeNotes: '',
}

function App() {
  const [dashboard, setDashboard] = useState({ summary: [], pipeline: [], recentTrades: [] })
  const [assets, setAssets] = useState([])
  const [watchlistEntries, setWatchlistEntries] = useState([])
  const [assetForm, setAssetForm] = useState(initialAssetForm)
  const [watchlistForm, setWatchlistForm] = useState(initialWatchlistForm)
  const [tradeForm, setTradeForm] = useState(initialTradeForm)
  const [searchTerm, setSearchTerm] = useState('')
  const [trackerFilter, setTrackerFilter] = useState('ALL')
  const [isLoading, setIsLoading] = useState(true)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [message, setMessage] = useState({ type: 'idle', text: '' })
  const deferredSearch = useDeferredValue(searchTerm)

  const readySetups = watchlistEntries.filter((entry) => entry.status === 'READY').length
  const activeAssets = assets.filter((asset) => asset.active).length
  const query = deferredSearch.trim().toLowerCase()

  const filteredWatchlist = watchlistEntries.filter((entry) => {
    const asset = assets.find((item) => item.id === entry.assetId)
    const matchesFilter = trackerFilter === 'ALL' || entry.status === trackerFilter
    const matchesSearch =
      query.length === 0 ||
      entry.symbol.toLowerCase().includes(query) ||
      entry.assetName.toLowerCase().includes(query) ||
      entry.setupNotes.toLowerCase().includes(query) ||
      (asset?.thesis ?? '').toLowerCase().includes(query)

    return matchesFilter && matchesSearch
  })

  useEffect(() => {
    let cancelled = false

    async function bootstrap() {
      try {
        const [dashboardData, assetData, watchlistData] = await fetchSnapshot()
        if (cancelled) {
          return
        }

        startTransition(() => {
          setDashboard(dashboardData)
          setAssets(assetData)
          setWatchlistEntries(watchlistData)
          setTradeForm((current) => ({
            ...current,
            executionPrice: assetData[0]?.currentPrice ?? current.executionPrice,
          }))
        })
      } catch (error) {
        if (!cancelled) {
          setMessage({ type: 'error', text: error.message })
        }
      } finally {
        if (!cancelled) {
          setIsLoading(false)
        }
      }
    }

    bootstrap()

    return () => {
      cancelled = true
    }
  }, [])

  async function handleAssetSubmit(event) {
    event.preventDefault()
    setIsSubmitting(true)

    try {
      await apiRequest('/api/assets', {
        method: 'POST',
        body: JSON.stringify({
          ...assetForm,
          currentPrice: Number(assetForm.currentPrice),
          dailyChangePercent: Number(assetForm.dailyChangePercent),
        }),
      })

      setAssetForm(initialAssetForm)
      setMessage({ type: 'success', text: 'Asset added to the tracker.' })
      await refreshData({ setDashboard, setAssets, setWatchlistEntries, setIsLoading, setMessage })
    } catch (error) {
      setMessage({ type: 'error', text: error.message })
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleWatchlistSubmit(event) {
    event.preventDefault()
    setIsSubmitting(true)

    try {
      await apiRequest('/api/watchlist', {
        method: 'POST',
        body: JSON.stringify({
          ...watchlistForm,
          assetId: Number(watchlistForm.assetId),
          targetPrice: Number(watchlistForm.targetPrice),
          convictionScore: Number(watchlistForm.convictionScore),
        }),
      })

      setWatchlistForm(initialWatchlistForm)
      setMessage({ type: 'success', text: 'Watchlist setup saved.' })
      await refreshData({ setDashboard, setAssets, setWatchlistEntries, setIsLoading, setMessage })
    } catch (error) {
      setMessage({ type: 'error', text: error.message })
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleTradeSubmit(event) {
    event.preventDefault()
    setIsSubmitting(true)

    try {
      await apiRequest('/api/trades', {
        method: 'POST',
        body: JSON.stringify({
          ...tradeForm,
          assetId: Number(tradeForm.assetId),
          quantity: Number(tradeForm.quantity),
          executionPrice: Number(tradeForm.executionPrice),
        }),
      })

      setTradeForm(initialTradeForm)
      setMessage({ type: 'success', text: 'Paper trade logged successfully.' })
      await refreshData({ setDashboard, setAssets, setWatchlistEntries, setIsLoading, setMessage })
    } catch (error) {
      setMessage({ type: 'error', text: error.message })
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="app-shell">
      <header className="topbar panel">
        <div className="topbar-copy">
          <p className="kicker">PaperTrade Hub</p>
          <h1>Watchlist and paper trading desk</h1>
          <p className="topbar-text">
            Track conviction setups, market context and paper trade execution from a single
            internal dashboard.
          </p>
        </div>
        <div className="topbar-side">
          <article className="status-note">
            <span className="note-label">System</span>
            <strong>{isLoading ? 'Refreshing market snapshot' : 'Market tracker is live'}</strong>
            <p>Local Spring Boot API and React dashboard are connected.</p>
          </article>
          <article className="status-note">
            <span className="note-label">Snapshot</span>
            <strong>
              {activeAssets} active assets, {readySetups} ready setups
            </strong>
            <p>{dashboard.recentTrades.length} recent paper trades are visible on the desk.</p>
          </article>
        </div>
      </header>

      {message.text ? (
        <div className={`alert alert-${message.type}`}>
          <span>{message.text}</span>
          <button type="button" onClick={() => setMessage({ type: 'idle', text: '' })}>
            Dismiss
          </button>
        </div>
      ) : null}

      <section className="summary-row">
        {dashboard.summary.map((card) => (
          <article key={card.label} className={`summary-card accent-${card.accent}`}>
            <span className="summary-label">{card.label}</span>
            <strong>{card.value}</strong>
          </article>
        ))}
      </section>

      <section className="workspace">
        <main className="workspace-main">
          <section className="panel tracker-panel">
            <div className="panel-header panel-header-wide">
              <div>
                <p className="section-tag">Watchlist monitor</p>
                <h2>Setup board</h2>
                <p className="panel-copy">
                  Review setup notes, target prices and conviction before logging a paper trade.
                </p>
              </div>
              <div className="toolbar">
                <input
                  value={searchTerm}
                  onChange={(event) => setSearchTerm(event.target.value)}
                  placeholder="Search symbol, thesis or setup notes"
                />
                <select
                  value={trackerFilter}
                  onChange={(event) => setTrackerFilter(event.target.value)}
                >
                  {trackerFilters.map((option) => (
                    <option key={option} value={option}>
                      {option === 'ALL' ? 'All setups' : formatLabel(option)}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="pipeline-strip">
              {dashboard.pipeline.map((item) => (
                <div key={item.status} className="pipeline-chip">
                  <span>{formatLabel(item.status)}</span>
                  <strong>{item.total}</strong>
                </div>
              ))}
            </div>

            <div className="application-list">
              {filteredWatchlist.length > 0 ? (
                filteredWatchlist.map((entry) => {
                  const asset = assets.find((item) => item.id === entry.assetId)
                  const statusMeta = getStatusMeta(entry.status)

                  return (
                    <article key={entry.id} className="application-card">
                      <div className="application-top">
                        <div>
                          <p className="application-name">
                            {entry.symbol} - {entry.assetName}
                          </p>
                          <p className="application-role">
                            {asset?.assetClass ?? 'Asset'} - Price {formatCurrency(asset?.currentPrice ?? 0)}
                          </p>
                        </div>
                        <span className={`status-pill ${statusMeta.className}`}>
                          {statusMeta.label}
                        </span>
                      </div>
                      <div className="application-meta">
                        <span>Target {formatCurrency(entry.targetPrice)}</span>
                        <span>Conviction {entry.convictionScore}/100</span>
                        <span>{formatChange(asset?.dailyChangePercent ?? 0)} today</span>
                      </div>
                      <p className="application-notes">{entry.setupNotes}</p>
                      <div className="application-footer application-footer-stack">
                        <span className="muted">
                          Thesis: {asset?.thesis ?? 'No thesis added yet.'}
                        </span>
                        <span className="muted">Added {formatDate(entry.createdAt)}</span>
                      </div>
                    </article>
                  )
                })
              ) : (
                <div className="empty-state">
                  <strong>No setups match this filter.</strong>
                  <p>Try another status or clear the search field.</p>
                </div>
              )}
            </div>
          </section>

          <section className="resource-grid">
            <section className="panel compact-panel">
              <div className="panel-header">
                <div>
                  <p className="section-tag">Assets</p>
                  <h2>Market list</h2>
                </div>
                <span className="muted">{assets.length} tracked</span>
              </div>
              <div className="stack-list">
                {assets.map((asset) => (
                  <article key={asset.id} className="mini-card">
                    <strong>
                      {asset.symbol} - {asset.name}
                    </strong>
                    <p>
                      {asset.assetClass} - {formatCurrency(asset.currentPrice)}
                    </p>
                    <span>
                      {formatChange(asset.dailyChangePercent)} - {asset.active ? 'Active' : 'Paused'}
                    </span>
                  </article>
                ))}
              </div>
            </section>

            <section className="panel compact-panel">
              <div className="panel-header">
                <div>
                  <p className="section-tag">Recent execution</p>
                  <h2>Trade log</h2>
                </div>
                <span className="muted">{dashboard.recentTrades.length} recent</span>
              </div>
              <div className="stack-list">
                {dashboard.recentTrades.map((trade) => (
                  <article key={trade.id} className="mini-card">
                    <strong>
                      {trade.symbol} - {formatLabel(trade.side)}
                    </strong>
                    <p>
                      {trade.quantity} @ {formatCurrency(trade.executionPrice)}
                    </p>
                    <span>
                      {trade.strategyTag} - {formatDate(trade.executedAt)}
                    </span>
                  </article>
                ))}
              </div>
            </section>
          </section>
        </main>

        <aside className="workspace-side">
          <section className="panel form-panel">
            <div className="panel-header">
              <div>
                <p className="section-tag">Assets</p>
                <h2>Add market asset</h2>
              </div>
            </div>
            <form className="form-grid" onSubmit={handleAssetSubmit}>
              <label>
                Symbol
                <input
                  value={assetForm.symbol}
                  onChange={(event) =>
                    setAssetForm((current) => ({ ...current, symbol: event.target.value }))
                  }
                  required
                />
              </label>
              <label>
                Name
                <input
                  value={assetForm.name}
                  onChange={(event) =>
                    setAssetForm((current) => ({ ...current, name: event.target.value }))
                  }
                  required
                />
              </label>
              <label>
                Asset class
                <input
                  value={assetForm.assetClass}
                  onChange={(event) =>
                    setAssetForm((current) => ({ ...current, assetClass: event.target.value }))
                  }
                  required
                />
              </label>
              <label>
                Current price
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={assetForm.currentPrice}
                  onChange={(event) =>
                    setAssetForm((current) => ({
                      ...current,
                      currentPrice: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label>
                Daily change %
                <input
                  type="number"
                  step="0.01"
                  value={assetForm.dailyChangePercent}
                  onChange={(event) =>
                    setAssetForm((current) => ({
                      ...current,
                      dailyChangePercent: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label className="checkbox-row">
                <input
                  type="checkbox"
                  checked={assetForm.active}
                  onChange={(event) =>
                    setAssetForm((current) => ({ ...current, active: event.target.checked }))
                  }
                />
                Active tracking
              </label>
              <label className="wide">
                Thesis
                <textarea
                  rows="4"
                  value={assetForm.thesis}
                  onChange={(event) =>
                    setAssetForm((current) => ({ ...current, thesis: event.target.value }))
                  }
                  placeholder="Narrative, structure and risk context for this asset."
                  required
                />
              </label>
              <button className="primary-button" disabled={isSubmitting} type="submit">
                Save asset
              </button>
            </form>
          </section>

          <section className="panel form-panel">
            <div className="panel-header">
              <div>
                <p className="section-tag">Watchlist</p>
                <h2>Add setup</h2>
              </div>
            </div>
            <form className="form-grid" onSubmit={handleWatchlistSubmit}>
              <label className="wide">
                Asset
                <select
                  value={watchlistForm.assetId}
                  onChange={(event) =>
                    setWatchlistForm((current) => ({ ...current, assetId: event.target.value }))
                  }
                  required
                >
                  <option value="">Select asset</option>
                  {assets.map((asset) => (
                    <option key={asset.id} value={asset.id}>
                      {asset.symbol} - {asset.name}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Status
                <select
                  value={watchlistForm.status}
                  onChange={(event) =>
                    setWatchlistForm((current) => ({ ...current, status: event.target.value }))
                  }
                >
                  {watchlistStatuses.map((option) => (
                    <option key={option} value={option}>
                      {formatLabel(option)}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Target price
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={watchlistForm.targetPrice}
                  onChange={(event) =>
                    setWatchlistForm((current) => ({
                      ...current,
                      targetPrice: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label>
                Conviction score
                <input
                  type="number"
                  min="1"
                  max="100"
                  value={watchlistForm.convictionScore}
                  onChange={(event) =>
                    setWatchlistForm((current) => ({
                      ...current,
                      convictionScore: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label className="wide">
                Setup notes
                <textarea
                  rows="4"
                  value={watchlistForm.setupNotes}
                  onChange={(event) =>
                    setWatchlistForm((current) => ({
                      ...current,
                      setupNotes: event.target.value,
                    }))
                  }
                  placeholder="Trigger, invalidation and market structure notes."
                  required
                />
              </label>
              <button className="primary-button" disabled={isSubmitting} type="submit">
                Save setup
              </button>
            </form>
          </section>

          <section className="panel form-panel">
            <div className="panel-header">
              <div>
                <p className="section-tag">Execution</p>
                <h2>Log trade</h2>
              </div>
            </div>
            <form className="form-grid" onSubmit={handleTradeSubmit}>
              <label className="wide">
                Asset
                <select
                  value={tradeForm.assetId}
                  onChange={(event) =>
                    setTradeForm((current) => ({ ...current, assetId: event.target.value }))
                  }
                  required
                >
                  <option value="">Select asset</option>
                  {assets.map((asset) => (
                    <option key={asset.id} value={asset.id}>
                      {asset.symbol} - {asset.name}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Side
                <select
                  value={tradeForm.side}
                  onChange={(event) =>
                    setTradeForm((current) => ({ ...current, side: event.target.value }))
                  }
                >
                  {tradeSides.map((option) => (
                    <option key={option} value={option}>
                      {formatLabel(option)}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Quantity
                <input
                  type="number"
                  min="0.000001"
                  step="0.000001"
                  value={tradeForm.quantity}
                  onChange={(event) =>
                    setTradeForm((current) => ({ ...current, quantity: event.target.value }))
                  }
                  required
                />
              </label>
              <label>
                Execution price
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={tradeForm.executionPrice}
                  onChange={(event) =>
                    setTradeForm((current) => ({
                      ...current,
                      executionPrice: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label className="wide">
                Strategy tag
                <input
                  value={tradeForm.strategyTag}
                  onChange={(event) =>
                    setTradeForm((current) => ({
                      ...current,
                      strategyTag: event.target.value,
                    }))
                  }
                  placeholder="Breakout retest, mean reversion, trend continuation..."
                  required
                />
              </label>
              <label className="wide">
                Trade notes
                <textarea
                  rows="4"
                  value={tradeForm.tradeNotes}
                  onChange={(event) =>
                    setTradeForm((current) => ({
                      ...current,
                      tradeNotes: event.target.value,
                    }))
                  }
                  placeholder="Why the trade was taken and what invalidates the idea."
                  required
                />
              </label>
              <button className="primary-button" disabled={isSubmitting} type="submit">
                Save trade
              </button>
            </form>
          </section>
        </aside>
      </section>
    </div>
  )
}

async function apiRequest(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {}),
    },
    ...options,
  })

  if (!response.ok) {
    const errorPayload = await response.json().catch(() => ({}))
    const message =
      errorPayload.message ||
      (errorPayload.validationErrors
        ? Object.values(errorPayload.validationErrors).join(', ')
        : 'Request failed')
    throw new Error(message)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}

async function fetchSnapshot() {
  return Promise.all([
    apiRequest('/api/dashboard'),
    apiRequest('/api/assets'),
    apiRequest('/api/watchlist'),
  ])
}

async function refreshData({ setDashboard, setAssets, setWatchlistEntries, setIsLoading, setMessage }) {
  setIsLoading(true)

  try {
    const [dashboardData, assetData, watchlistData] = await Promise.all([
      apiRequest('/api/dashboard'),
      apiRequest('/api/assets'),
      apiRequest('/api/watchlist'),
    ])

    startTransition(() => {
      setDashboard(dashboardData)
      setAssets(assetData)
      setWatchlistEntries(watchlistData)
    })
  } catch (error) {
    setMessage({ type: 'error', text: error.message })
  } finally {
    setIsLoading(false)
  }
}

function getStatusMeta(status) {
  if (status === 'READY') {
    return { label: 'Ready', className: 'status-hired' }
  }

  if (status === 'PAUSED') {
    return { label: 'Paused', className: 'status-rejected' }
  }

  return { label: 'Active', className: 'status-screening' }
}

function formatLabel(value) {
  return value
    .toLowerCase()
    .split('_')
    .map((word) => word[0].toUpperCase() + word.slice(1))
    .join(' ')
}

function formatDate(value) {
  return new Intl.DateTimeFormat('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  }).format(new Date(value))
}

function formatCurrency(value) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: value < 10 ? 4 : 2,
  }).format(value)
}

function formatChange(value) {
  const prefix = Number(value) > 0 ? '+' : ''
  return `${prefix}${Number(value).toFixed(2)}%`
}

export default App
