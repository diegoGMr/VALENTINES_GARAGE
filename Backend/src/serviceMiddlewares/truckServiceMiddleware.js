const db = require('../config/db');

const validateTruckRegistration = async (req, _res, next) => {
  try {
    const {
      truck_id,
      vin,
      license_plate,
      truck_image_url,
      kilometers,
      client_id,
      speciality_id,
    } = req.body;

    if (!vin || !license_plate || !client_id || !speciality_id) {
      const error = new Error(
        'vin, license_plate, client_id, and speciality_id are required'
      );
      error.status = 400;
      throw error;
    }

    const specialityQuery = `SELECT id FROM speciality_trucks
			WHERE id = $1
			LIMIT 1`;
    const { rows: specialityRows } = await db.query(specialityQuery, [
      speciality_id,
    ]);

    if (specialityRows.length === 0) {
      const error = new Error('Truck speciality is not supported by company');
      error.status = 400;
      throw error;
    }

    const clientQuery = `SELECT client_id FROM clients
			WHERE client_id = $1
			LIMIT 1`;
    const { rows: clientRows } = await db.query(clientQuery, [client_id]);

    if (clientRows.length === 0) {
      const error = new Error('Client does not exist');
      error.status = 404;
      throw error;
    }

    const duplicateTruckQuery = `SELECT truck_id, vin, license_plate FROM trucks
			WHERE vin = $1 OR license_plate = $2
			LIMIT 1`;
    const { rows: duplicateTruckRows } = await db.query(duplicateTruckQuery, [
      vin,
      license_plate,
    ]);

    if (duplicateTruckRows.length > 0) {
      const duplicateTruck = duplicateTruckRows[0];

      if (duplicateTruck.vin === vin) {
        const error = new Error('VIN is already registered');
        error.status = 409;
        throw error;
      }

      if (duplicateTruck.license_plate === license_plate) {
        const error = new Error('License plate is already registered');
        error.status = 409;
        throw error;
      }
    }

    req.validatedTruckData = {
      truck_id,
      vin,
      license_plate,
      truck_image_url,
      kilometers,
      client_id,
      speciality_id,
    };

    next();
  } catch (err) {
    next(err);
  }
};

module.exports = {
  validateTruckRegistration,
};
