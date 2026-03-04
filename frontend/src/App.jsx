import React, { useMemo, useState } from "react";
import api from "./services/api";

export default function App() {
  const [username, setUsername] = useState("alice");
  const [password, setPassword] = useState("password");
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [productId, setProductId] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [version, setVersion] = useState("v1");
  const [message, setMessage] = useState("");

  const isAuthenticated = useMemo(() => !!localStorage.getItem("token"), [message]);

  const login = async () => {
    try {
      const response = await api.post("/api/v1/auth/login", { username, password });
      localStorage.setItem("token", response.data.token);
      setMessage("Login successful");
    } catch (error) {
      setMessage(error.response?.data?.message || "Login failed");
    }
  };

  const loadProducts = async () => {
    try {
      const response = await api.get(`/api/${version}/products`);
      setProducts(response.data);
      setMessage(`Loaded ${version} products`);
    } catch (error) {
      setMessage(error.response?.data?.message || "Failed to load products");
    }
  };

  const placeOrder = async () => {
    try {
      const response = await api.post("/api/v1/users/orders", {
        productId: Number(productId),
        quantity: Number(quantity)
      });
      setOrders((prev) => [response.data, ...prev]);
      setMessage("Order placed");
    } catch (error) {
      setMessage(error.response?.data?.message || "Order failed");
    }
  };

  const loadOrderStatus = async (id) => {
    try {
      const response = await api.get(`/api/v1/orders/${id}`);
      setOrders((prev) => prev.map((o) => (o.orderId === id ? response.data : o)));
      setMessage("Order status refreshed");
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to fetch order");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-cyan-50 via-white to-emerald-100 p-6">
      <div className="mx-auto max-w-5xl rounded-2xl bg-white shadow-xl p-6 space-y-6">
        <h1 className="text-3xl font-bold text-slate-900">E-commerce Microservices Demo</h1>
        <p className="text-sm text-slate-600">Gateway URL: http://localhost:8080</p>

        <div className="grid md:grid-cols-2 gap-4">
          <div className="rounded-xl border p-4 space-y-3">
            <h2 className="font-semibold">Authentication</h2>
            <input className="w-full border rounded p-2" value={username} onChange={(e) => setUsername(e.target.value)} placeholder="username" />
            <input className="w-full border rounded p-2" type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="password" />
            <button className="bg-slate-900 text-white px-4 py-2 rounded" onClick={login}>Login</button>
          </div>

          <div className="rounded-xl border p-4 space-y-3">
            <h2 className="font-semibold">Products</h2>
            <select className="w-full border rounded p-2" value={version} onChange={(e) => setVersion(e.target.value)}>
              <option value="v1">API v1</option>
              <option value="v2">API v2</option>
            </select>
            <button className="bg-cyan-700 text-white px-4 py-2 rounded" onClick={loadProducts}>Load Products</button>
            <ul className="space-y-2 text-sm">
              {products.map((p) => (
                <li className="rounded border p-2" key={p.id}>{p.name} - ${p.price} {p.stock !== undefined ? `(stock: ${p.stock})` : ""}</li>
              ))}
            </ul>
          </div>
        </div>

        {isAuthenticated && (
          <div className="rounded-xl border p-4 space-y-3">
            <h2 className="font-semibold">Place Order</h2>
            <div className="grid md:grid-cols-3 gap-2">
              <input className="border rounded p-2" value={productId} onChange={(e) => setProductId(e.target.value)} placeholder="Product ID" />
              <input className="border rounded p-2" type="number" value={quantity} onChange={(e) => setQuantity(e.target.value)} />
              <button className="bg-emerald-700 text-white px-4 py-2 rounded" onClick={placeOrder}>Place</button>
            </div>
          </div>
        )}

        <div className="rounded-xl border p-4 space-y-3">
          <h2 className="font-semibold">Order Status</h2>
          <ul className="space-y-2 text-sm">
            {orders.map((o) => (
              <li className="rounded border p-2 flex items-center justify-between" key={o.orderId}>
                <span>Order #{o.orderId} | Status: {o.status} | Payment: {o.paymentStatus}</span>
                <button className="bg-slate-700 text-white px-2 py-1 rounded" onClick={() => loadOrderStatus(o.orderId)}>Refresh</button>
              </li>
            ))}
          </ul>
        </div>

        {message && <p className="text-sm text-blue-800">{message}</p>}
      </div>
    </div>
  );
}
