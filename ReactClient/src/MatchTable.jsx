export default function MatchTable({ matches, editFunc, deleteFunc }) {
    return (
        <table border="1" cellPadding="5">
            <thead>
            <tr>
                <th>ID</th><th>Team A</th><th>Team B</th>
                <th>Price</th><th>Seats</th><th>Actions</th>
            </tr>
            </thead>
            <tbody>
            {matches.map(m => (
                <tr key={m.id}>
                    <td>{m.id}</td>
                    <td>{m.teamA}</td>
                    <td>{m.teamB}</td>
                    <td>{m.ticketPrice}</td>
                    <td>{m.availableSeats}</td>
                    <td className="actions">
                        <button onClick={() => editFunc(m)}>Edit</button>
                        <button className="delete" onClick={() => deleteFunc(m.id)}>
                            Delete
                        </button>
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}
