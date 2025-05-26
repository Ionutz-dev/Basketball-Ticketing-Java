import { useState, useEffect } from "react";
import { GetMatches, AddMatch, UpdateMatch, DeleteMatch } from "./utils/rest-calls";
import MatchForm from "./MatchForm";
import MatchTable from "./MatchTable";

export default function MatchApp() {
    const [matches, setMatches] = useState([]);
    const [editing, setEditing] = useState(null);

    useEffect(() => {
        GetMatches().then(setMatches).catch(console.error);
    }, []);

    const addFunc = (newMatch) =>
        AddMatch(newMatch)
            .then(() => GetMatches())
            .then(setMatches)
            .catch(err => console.error("AddMatch failed:", err));

    const updateFunc = (id, m) =>
        UpdateMatch(id, m)
            .then(() => GetMatches())
            .then(all => {
                setMatches(all);
                setEditing(null);
            });

    const deleteFunc = (id) =>
        DeleteMatch(id).then(() =>
            setMatches(matches.filter(match => match.id !== id))
        );

    return (
        <div className="container">
            <h1>Basketball Matches</h1>
            <MatchForm
                addFunc={addFunc}
                updateFunc={updateFunc}
                editing={editing}
                cancelEdit={() => setEditing(null)}
            />
            <MatchTable
                matches={matches}
                editFunc={setEditing}
                deleteFunc={deleteFunc}
            />
        </div>
    );
}
