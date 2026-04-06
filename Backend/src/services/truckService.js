const db = require('../config/db');
const generateRandomHex = require('../helpers/idHelper');

// Register a new truck
const registerTruck = async (truckData) => {
  const {
    truck_id,
    vin,
    license_plate,
    truck_image_url,
    kilometers,
    last_service_date,
    next_service_due,
    client_id,
    speciality_id,
  } = truckData;

  const resolvedTruckId = truck_id || generateRandomHex(6);
  const query = `INSERT INTO trucks (
    truck_id, vin, license_plate, truck_image_url, kilometers,
    last_service_date, next_service_due, client_id, speciality_id
  ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)`;

  const { rowCount } = await db.query(query, [
    resolvedTruckId,
    vin,
    license_plate,
    truck_image_url || null,
    kilometers || null,
    last_service_date || null,
    next_service_due || null,
    client_id,
    speciality_id,
  ]);
  return {
    truck_id: resolvedTruckId,
    affectedRows: rowCount,
  };
};

// Get truck by truck_id
const getTruckById = async (truck_id) => {
  const query = 'SELECT * FROM trucks WHERE truck_id = $1';
  const { rows } = await db.query(query, [truck_id]);
  return rows[0] || null;
};

module.exports = {
  registerTruck,
  getTruckById,
};
