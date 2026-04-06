const db = require('../config/db');

const validateClientRegistration = async (req, _res, next) => {
  try {
    const { full_name, email, phone, address, company_name } = req.body;

    if (!full_name || !phone) {
      const error = new Error('full_name and phone are required');
      error.status = 400;
      throw error;
    }

    if (email) {
      const { rows: existingClients } = await db.query(
        'SELECT client_id FROM clients WHERE email = $1 LIMIT 1',
        [email]
      );

      if (existingClients.length > 0) {
        const error = new Error('Client email already registered');
        error.status = 409;
        throw error;
      }
    }

    req.validatedClientData = {
      full_name,
      email: email || null,
      phone,
      address: address || null,
      company_name: company_name || null,
    };

    next();
  } catch (err) {
    next(err);
  }
};

const validateNewClientVisit = async (req, _res, next) => {
  try {
    const {
      client_id,
      truck_id,
      service_type,
      client_notes,
      visit_reference,
    } = req.body;

    if (!client_id || !truck_id || !service_type || !client_notes) {
      const error = new Error(
        'client_id, truck_id, service_type, and client_notes are required'
      );
      error.status = 400;
      throw error;
    }

    if (!['service', 'repair'].includes(service_type)) {
      const error = new Error('service_type must be either "service" or "repair"');
      error.status = 400;
      throw error;
    }

    const { rows: existingClients } = await db.query(
      'SELECT client_id FROM clients WHERE client_id = $1 LIMIT 1',
      [client_id]
    );

    if (existingClients.length === 0) {
      const error = new Error('Client not found');
      error.status = 404;
      throw error;
    }

    const { rows: existingTrucks } = await db.query(
      'SELECT truck_id FROM trucks WHERE truck_id = $1 LIMIT 1',
      [truck_id]
    );

    if (existingTrucks.length === 0) {
      const error = new Error('Truck not found');
      error.status = 404;
      throw error;
    }

    req.validatedVisitData = {
      client_id,
      truck_id,
      service_type,
      client_notes,
      visit_reference: visit_reference || null,
    };

    next();
  } catch (err) {
    next(err);
  }
};

module.exports = {
  validateClientRegistration,
  validateNewClientVisit,
};
