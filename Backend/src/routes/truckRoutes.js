const { Router } = require('express');
const truckService = require('../services/truckService');
const middleware = require('../serviceMiddlewares/truckServiceMiddleware');

const router = Router();

router.post('/truck/register', middleware.validateTruckRegistration, async (req, res, next) => {
	try {
		const result = await truckService.registerTruck(req.validatedTruckData);
		res.status(201).json(result);
	} catch (err) {
		next(err);
	}
});

router.get('/truck/getTruck/:id', async (req, res, next) => {
	try {
		const truck = await truckService.getTruckById(req.params.id);
		if (!truck) return res.status(404).json({ message: 'Truck not found' });
		res.status(200).json(truck);
	} catch (err) {
		next(err);
	}
});

module.exports = router;
