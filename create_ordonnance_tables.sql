-- Create ordonnance table
CREATE TABLE IF NOT EXISTS ordonnance (
    ordonnanceID INT AUTO_INCREMENT PRIMARY KEY,
    nomClient VARCHAR(255) NOT NULL,
    dateOrdonnance DATETIME DEFAULT CURRENT_TIMESTAMP,
    pharmacienID INT,
    total DOUBLE DEFAULT 0,
    FOREIGN KEY (pharmacienID) REFERENCES pharmacien(id)
);

-- Create ligneordonnance table
CREATE TABLE IF NOT EXISTS ligneordonnance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ordonnanceID INT NOT NULL,
    produitID INT NOT NULL,
    quantite INT NOT NULL,
    prix DOUBLE NOT NULL,
    FOREIGN KEY (ordonnanceID) REFERENCES ordonnance(ordonnanceID),
    FOREIGN KEY (produitID) REFERENCES produit(produitid)
);
