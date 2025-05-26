import { MATCHES_BASE_URL } from "./consts";

function status(res) {
    if (res.status >= 200 && res.status < 300) return res;
    throw new Error(res.statusText);
}

function json(res) {
    return res.json();
}

export function GetMatches() {
    return fetch(MATCHES_BASE_URL, { mode: "cors" })
        .then(status)
        .then(json);
}

export function AddMatch(m) {
    return fetch(MATCHES_BASE_URL, {
        method: "POST",
        mode: "cors",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(m),
    }).then(status);
}

export function UpdateMatch(id, m) {
    return fetch(`${MATCHES_BASE_URL}/${id}`, {
        method: "PUT",
        mode: "cors",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(m),
    }).then(status);
}

export function DeleteMatch(id) {
    return fetch(`${MATCHES_BASE_URL}/${id}`, {
        method: "DELETE",
        mode: "cors",
    }).then(status);
}
