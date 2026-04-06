const { Router } = require('express');
const clientService = require('../services/clientService');
const { clientMiddleware } = require('../serviceMiddlewares');

const router = Router();

router.post('/client/register', clientMiddleware.validateClientRegistration, async (req, res, next) => {
	try {
		const clientId = await clientService.registerClient(req.validatedClientData);
		res.status(201).json({ clientId });
	} catch (err) {
		next(err);
	}
});

router.post('/client/update', async (req, res, next) => {
	try {
		const affectedRows = await clientService.updateClient(req.body.clientId, req.body);
		res.status(200).json({ affectedRows });
	} catch (err) {
		next(err);
	}
});

router.get('/client/getClientById/:clientId', async (req, res, next) => {
	try {
		const client = await clientService.fetchClientById(req.params.clientId);
		if (!client) return res.status(404).json({ message: 'Client not found' });
		res.status(200).json(client);
	} catch (err) {
		next(err);
	}
});

router.get('/client/getClientByHex/:hexId', async (req, res, next) => {
	try {
		const client = await clientService.fetchClientByHexId(req.params.hexId);
		if (!client) return res.status(404).json({ message: 'Client not found' });
		res.status(200).json(client);
	} catch (err) {
		next(err);
	}
});

router.post('/client/newVisit', clientMiddleware.validateNewClientVisit, async (req, res, next) => {
	try {
		const visitId = await clientService.createClientVisit(req.validatedVisitData);
		res.status(201).json({ visitId });
	} catch (err) {
		next(err);
	}
});

module.exports = router;
