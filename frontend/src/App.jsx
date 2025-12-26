import { useEffect, useMemo, useState } from 'react'
import './App.css'

const today = new Date()
const defaultMonth = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`

function App() {
  const [expenses, setExpenses] = useState([])
  const [budgets, setBudgets] = useState([])
  const [monthlySummary, setMonthlySummary] = useState([])
  const [categorySummary, setCategorySummary] = useState([])
  const [alerts, setAlerts] = useState([])
  const [selectedMonth, setSelectedMonth] = useState(defaultMonth)
  const [selectedYear, setSelectedYear] = useState(String(today.getFullYear()))
  const [editingId, setEditingId] = useState(null)
  const [error, setError] = useState('')

  const [expenseForm, setExpenseForm] = useState({
    amount: '',
    category: '',
    description: '',
    expenseDate: new Date().toISOString().slice(0, 10),
  })

  const [budgetForm, setBudgetForm] = useState({
    category: '',
    monthlyLimit: '',
  })

  const currency = useMemo(
    () =>
      new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
      }),
    []
  )

  const monthTotal = useMemo(() => {
    return categorySummary.reduce((sum, item) => sum + Number(item.total), 0)
  }, [categorySummary])

  const overBudgetCount = useMemo(() => {
    return alerts.filter((alert) => alert.status === 'OVER').length
  }, [alerts])

  useEffect(() => {
    loadExpenses()
    loadBudgets()
  }, [])

  useEffect(() => {
    if (selectedYear) {
      loadMonthlySummary(selectedYear)
    }
  }, [selectedYear])

  useEffect(() => {
    if (selectedMonth) {
      loadCategorySummary(selectedMonth)
      loadAlerts(selectedMonth)
    }
  }, [selectedMonth])

  const handleApiError = (message) => {
    setError(message)
    setTimeout(() => setError(''), 4000)
  }

  const loadExpenses = async () => {
    const res = await fetch('/api/expenses')
    if (!res.ok) {
      handleApiError('Unable to load expenses.')
      return
    }
    const data = await res.json()
    setExpenses(data)
  }

  const loadBudgets = async () => {
    const res = await fetch('/api/budgets')
    if (!res.ok) {
      handleApiError('Unable to load budgets.')
      return
    }
    const data = await res.json()
    setBudgets(data)
  }

  const loadMonthlySummary = async (year) => {
    const res = await fetch(`/api/summary/monthly?year=${year}`)
    if (!res.ok) {
      handleApiError('Unable to load monthly summary.')
      return
    }
    const data = await res.json()
    setMonthlySummary(data)
  }

  const loadCategorySummary = async (month) => {
    const res = await fetch(`/api/summary/categories?month=${month}`)
    if (!res.ok) {
      handleApiError('Unable to load category summary.')
      return
    }
    const data = await res.json()
    setCategorySummary(data)
  }

  const loadAlerts = async (month) => {
    const res = await fetch(`/api/summary/alerts?month=${month}`)
    if (!res.ok) {
      handleApiError('Unable to load budget alerts.')
      return
    }
    const data = await res.json()
    setAlerts(data)
  }

  const resetExpenseForm = () => {
    setExpenseForm({
      amount: '',
      category: '',
      description: '',
      expenseDate: new Date().toISOString().slice(0, 10),
    })
    setEditingId(null)
  }

  const handleExpenseSubmit = async (event) => {
    event.preventDefault()
    setError('')

    const payload = {
      ...expenseForm,
      amount: Number(expenseForm.amount),
    }

    const url = editingId ? `/api/expenses/${editingId}` : '/api/expenses'
    const method = editingId ? 'PUT' : 'POST'

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })

    if (!res.ok) {
      handleApiError('Unable to save expense. Check the form and try again.')
      return
    }

    resetExpenseForm()
    await loadExpenses()
    await loadCategorySummary(selectedMonth)
    await loadAlerts(selectedMonth)
    await loadMonthlySummary(selectedYear)
  }

  const handleExpenseEdit = (expense) => {
    setEditingId(expense.id)
    setExpenseForm({
      amount: expense.amount,
      category: expense.category,
      description: expense.description || '',
      expenseDate: expense.expenseDate,
    })
  }

  const handleExpenseDelete = async (id) => {
    const res = await fetch(`/api/expenses/${id}`, { method: 'DELETE' })
    if (!res.ok) {
      handleApiError('Unable to delete expense.')
      return
    }
    await loadExpenses()
    await loadCategorySummary(selectedMonth)
    await loadAlerts(selectedMonth)
    await loadMonthlySummary(selectedYear)
  }

  const handleBudgetSubmit = async (event) => {
    event.preventDefault()
    setError('')

    const payload = {
      ...budgetForm,
      monthlyLimit: Number(budgetForm.monthlyLimit),
    }

    const res = await fetch('/api/budgets', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })

    if (!res.ok) {
      handleApiError('Unable to save budget.')
      return
    }

    setBudgetForm({ category: '', monthlyLimit: '' })
    await loadBudgets()
    await loadAlerts(selectedMonth)
  }

  const exportCsv = () => {
    window.location.href = `/api/expenses/export?month=${selectedMonth}`
  }

  return (
    <div className="app">
      <header className="hero">
        <div>
          <p className="eyebrow">Expense Tracker</p>
          <h1>Budget clarity for busy months.</h1>
          <p className="subtitle">
            Track spending, set category budgets, and export monthly data in seconds.
          </p>
        </div>
        <div className="hero-card">
          <div className="hero-stat">
            <span>Tracked Expenses</span>
            <strong>{expenses.length}</strong>
          </div>
          <div className="hero-stat">
            <span>Active Budgets</span>
            <strong>{budgets.length}</strong>
          </div>
          <div className="hero-stat">
            <span>Selected Month</span>
            <strong>{selectedMonth}</strong>
          </div>
          <div className="hero-stat">
            <span>Spent This Month</span>
            <strong>{currency.format(monthTotal)}</strong>
          </div>
          <div className="hero-stat">
            <span>Over Budget</span>
            <strong>{overBudgetCount}</strong>
          </div>
        </div>
      </header>

      {error && <div className="banner">{error}</div>}

      <section className="grid">
        <div className="panel">
          <h2>{editingId ? 'Edit expense' : 'Add expense'}</h2>
          <form onSubmit={handleExpenseSubmit} className="form">
            <label>
              Amount
              <input
                type="number"
                step="0.01"
                min="0"
                value={expenseForm.amount}
                onChange={(event) =>
                  setExpenseForm((prev) => ({ ...prev, amount: event.target.value }))
                }
                required
              />
            </label>
            <label>
              Category
              <input
                type="text"
                value={expenseForm.category}
                onChange={(event) =>
                  setExpenseForm((prev) => ({ ...prev, category: event.target.value }))
                }
                required
              />
            </label>
            <label>
              Description
              <input
                type="text"
                value={expenseForm.description}
                onChange={(event) =>
                  setExpenseForm((prev) => ({ ...prev, description: event.target.value }))
                }
              />
            </label>
            <label>
              Date
              <input
                type="date"
                value={expenseForm.expenseDate}
                onChange={(event) =>
                  setExpenseForm((prev) => ({ ...prev, expenseDate: event.target.value }))
                }
                required
              />
            </label>
            <div className="actions">
              <button type="submit">{editingId ? 'Update expense' : 'Add expense'}</button>
              {editingId && (
                <button type="button" className="ghost" onClick={resetExpenseForm}>
                  Cancel
                </button>
              )}
            </div>
          </form>
        </div>

        <div className="panel">
          <h2>Monthly insights</h2>
          <div className="filters">
            <label>
              Year
              <input
                type="number"
                value={selectedYear}
                onChange={(event) => setSelectedYear(event.target.value)}
                min="2000"
                max="2100"
              />
            </label>
            <label>
              Month
              <input
                type="month"
                value={selectedMonth}
                onChange={(event) => setSelectedMonth(event.target.value)}
              />
            </label>
            <button className="ghost" onClick={exportCsv}>
              Export CSV
            </button>
          </div>

          <div className="summary-grid">
            <div>
              <h3>Monthly totals</h3>
              <ul className="summary-list">
                {monthlySummary.length === 0 && <li>No data yet.</li>}
                {monthlySummary.map((item) => (
                  <li key={item.month}>
                    <span>{item.month}</span>
                    <strong>{currency.format(item.total)}</strong>
                  </li>
                ))}
              </ul>
            </div>
            <div>
              <h3>Category breakdown</h3>
              <ul className="summary-list">
                {categorySummary.length === 0 && <li>No data yet.</li>}
                {categorySummary.map((item) => (
                  <li key={item.category}>
                    <span>{item.category}</span>
                    <strong>{currency.format(item.total)}</strong>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>

        <div className="panel">
          <h2>Budgets</h2>
          <form onSubmit={handleBudgetSubmit} className="form inline">
            <label>
              Category
              <input
                type="text"
                value={budgetForm.category}
                onChange={(event) =>
                  setBudgetForm((prev) => ({ ...prev, category: event.target.value }))
                }
                required
              />
            </label>
            <label>
              Monthly limit
              <input
                type="number"
                step="0.01"
                min="0"
                value={budgetForm.monthlyLimit}
                onChange={(event) =>
                  setBudgetForm((prev) => ({ ...prev, monthlyLimit: event.target.value }))
                }
                required
              />
            </label>
            <button type="submit">Save budget</button>
          </form>

          <div className="alert-grid">
            {alerts.length === 0 && <p className="muted">Set a budget to see alerts.</p>}
            {alerts.map((alert) => (
              <div key={alert.category} className="alert-card" data-status={alert.status}>
                <div>
                  <h4>{alert.category}</h4>
                  <p>
                    {currency.format(alert.spent)} of {currency.format(alert.budget)}
                  </p>
                </div>
                <span className="pill">{alert.status}</span>
                <span className="percent">{alert.percentUsed}%</span>
                <div className="bar">
                  <span style={{ width: `${Math.min(alert.percentUsed, 100)}%` }} />
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="panel">
        <div className="table-header">
          <h2>All expenses</h2>
          <p className="muted">Click an expense to edit it.</p>
        </div>
        <div className="table">
          <div className="table-row table-head">
            <span>Date</span>
            <span>Category</span>
            <span>Description</span>
            <span>Amount</span>
            <span>Actions</span>
          </div>
          {expenses.length === 0 && <div className="table-row empty">No expenses yet.</div>}
          {expenses.map((expense) => (
            <div key={expense.id} className="table-row">
              <span>{expense.expenseDate}</span>
              <span>{expense.category}</span>
              <span>{expense.description || '-'}</span>
              <span>{currency.format(expense.amount)}</span>
              <span className="table-actions">
                <button className="ghost" onClick={() => handleExpenseEdit(expense)}>
                  Edit
                </button>
                <button className="danger" onClick={() => handleExpenseDelete(expense.id)}>
                  Delete
                </button>
              </span>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}

export default App
