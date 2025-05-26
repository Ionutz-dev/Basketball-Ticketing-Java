import { useState, useEffect } from "react";

export default function MatchForm({ addFunc, updateFunc, editing, cancelEdit }) {
    const [teamA, setTeamA] = useState("");
    const [teamB, setTeamB] = useState("");
    const [ticketPrice, setTicketPrice] = useState("");
    const [availableSeats, setAvailableSeats] = useState("");

    useEffect(() => {
        if (editing) {
            setTeamA(editing.teamA);
            setTeamB(editing.teamB);
            setTicketPrice(editing.ticketPrice);
            setAvailableSeats(editing.availableSeats);
        } else {
            setTeamA("");
            setTeamB("");
            setTicketPrice("");
            setAvailableSeats("");
        }
    }, [editing]);

    function handleSubmit(e) {
        e.preventDefault();
        const payload = {
            teamA,
            teamB,
            ticketPrice: parseFloat(ticketPrice),
            availableSeats: parseInt(availableSeats, 10),
        };
        if (editing) updateFunc(editing.id, payload);
        else addFunc(payload);
    }

    return (
        <form onSubmit={handleSubmit}>
            <h2>{editing ? "Edit Match" : "New Match"}</h2>
            <div className="form-grid">
                <label>
                    Team A
                    <input value={teamA} onChange={e => setTeamA(e.target.value)} required/>
                </label>
                <label>
                    Team B
                    <input value={teamB} onChange={e => setTeamB(e.target.value)} required/>
                </label>
                <label>
                    Ticket Price
                    <input
                        type="number"
                        step="0.01"
                        value={ticketPrice}
                        onChange={e => setTicketPrice(e.target.value)}
                        required
                    />
                </label>
                <label>
                    Available Seats
                    <input
                        type="number"
                        value={availableSeats}
                        onChange={e => setAvailableSeats(e.target.value)}
                        required
                    />
                </label>
            </div>
            <button type="submit">{editing ? "Update" : "Add"}</button>
            {editing && (
                <button type="button" className="cancel" onClick={cancelEdit}>
                    Cancel
                </button>
            )}
        </form>
    );
}
