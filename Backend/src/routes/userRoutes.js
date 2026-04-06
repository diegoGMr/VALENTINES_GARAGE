const { Router } = require('express');
const userService = require('../services/userService');
const { userMiddleware } = require('../serviceMiddlewares');

const router = Router();

router.post('/user/registerUser', userMiddleware.validateUserRegistration, async (req, res, next) => {
	try {
		const userId = await userService.registerUser(req.validatedUserData);
		res.status(201).json({ userId });
	} catch (err) {
		next(err);
	}
});

router.post('/user/loginUser', userMiddleware.validateUserLogin, async (req, res, next) => {
	try {
		const result = await userService.loginUser(req.validatedLoginData);
		res.status(200).json(result);
	} catch (err) {
		next(err);
	}
});

router.put('/user/updateUser/:id', userMiddleware.validateUserUpdateRequest, async (req, res, next) => {
	try {
		const affectedRows = await userService.updateUser(req.params.id, req.validatedUserUpdateData);
		res.status(200).json({ affectedRows });
	} catch (err) {
		next(err);
	}
});

router.get('/user/getUserWithId/:id', userMiddleware.validateUserIdParamMiddleware, async (req, res, next) => {
	try {
		const user = await userService.getUser(req.params.id);
		res.status(200).json(user);
	} catch (err) {
		next(err);
	}
});

module.exports = router;
