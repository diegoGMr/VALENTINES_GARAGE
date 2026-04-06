const db = require('../config/db');
const generateRandomHex = require('../helpers/idHelper');

// This service provides functions to manage clients and their visits in the Truck App

// Fetch client by client_id
const fetchClientById = async (client_id) => {
  const query = 'SELECT * FROM clients WHERE client_id = $1';
  try {
    const { rows } = await db.query(query, [client_id]);
    return rows[0] || null;
  } catch (error) {
    throw new Error(`Error fetching client by ID: ${error.message}`);
  }
};

// Fetch clients by hex_id
const fetchClientByHexId = async (hex_id) => {
  const query = 'SELECT * FROM clients WHERE hex_id = $1';
  try {
    const { rows } = await db.query(query, [hex_id]);
    return rows[0] || null;
  } catch (error) {
    throw new Error(`Error fetching client by hex ID: ${error.message}`);
  }
};

// Register a new client
const registerClient = async (clientData) => {
  const { full_name, email, phone, address, company_name } = clientData;
  const hex_id = generateRandomHex(6);

  const query =
    'INSERT INTO clients (hex_id, full_name, email, phone, address, company_name) VALUES ($1, $2, $3, $4, $5, $6) RETURNING client_id';

  try {
    const { rows } = await db.query(query, [
      hex_id,
      full_name,
      email,
      phone,
      address,
      company_name,
    ]);
    return rows[0].client_id;
  } catch (error) {
    throw new Error(`Error registering client: ${error.message}`);
  }
};

// Update client information
const updateClient = async (clientId, clientData) => {
  const { full_name, email, phone, address, company_name } = clientData;
  const query =
    'UPDATE clients SET full_name = $1, email = $2, phone = $3, address = $4, company_name = $5 WHERE client_id = $6';

  try {
    const { rowCount } = await db.query(query, [
      full_name,
      email,
      phone,
      address,
      company_name,
      clientId,
    ]);
    return rowCount;
  } catch (error) {
    throw new Error(`Error updating client: ${error.message}`);
  }
};

// Create a new client visit
const createClientVisit = async (visitData) => {
  const { client_id, truck_id, service_type, client_notes } = visitData;
  const visitReference = visitData.visit_reference || generateRandomHex(16);

  const query =
    'INSERT INTO visits (client_id, truck_id, service_type, client_notes, visit_reference) VALUES ($1, $2, $3, $4, $5) RETURNING visit_id';

  try {
    const { rows } = await db.query(query, [
      client_id,
      truck_id,
      service_type,
      client_notes,
      visitReference,
    ]);
    return rows[0].visit_id;
  } catch (error) {
    throw new Error(`Error creating client visit: ${error.message}`);
  }
};

module.exports = {
  registerClient,
  createClientVisit,
  updateClient,
  fetchClientById,
  fetchClientByHexId,
};
