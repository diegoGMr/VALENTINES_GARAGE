const db = require('../config/db');

const VALID_ROLES = new Set(['admin', 'mechanic', 'lead_mechanic', 'inspector', 'clerk']);

const validateUserRegistration = async (req, _res, next) => {
  try {
    const { name, email, password, phone, role } = req.body;

    if (!name || !email || !password || !role) {
      const error = new Error('name, email, password, and role are required');
      error.status = 400;
      throw error;
    }

    if (!VALID_ROLES.has(role)) {
      const error = new Error(
        'role must be admin, mechanic, lead_mechanic, inspector, or clerk'
      );
      error.status = 400;
      throw error;
    }

    const { rows: existingUsers } = await db.query(
      'SELECT user_id FROM users WHERE email = $1 LIMIT 1',
      [email]
    );

    if (existingUsers.length > 0) {
      const error = new Error('Email already registered');
      error.status = 409;
      throw error;
    }

    req.validatedUserData = {
      name,
      email,
      password,
      phone: phone || null,
      role,
    };

    next();
  } catch (err) {
    next(err);
  }
};

const validateUserLogin = async (req, _res, next) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      const error = new Error('email and password are required');
      error.status = 400;
      throw error;
    }

    req.validatedLoginData = {
      email,
      password,
    };

    next();
  } catch (err) {
    next(err);
  }
};

const validateUserIdParam = async (userId) => {
  if (!/^\d+$/.test(String(userId))) {
    const error = new Error('User id must be a valid integer');
    error.status = 400;
    throw error;
  }

  const { rows: existingUsers } = await db.query(
    'SELECT user_id FROM users WHERE user_id = $1 LIMIT 1',
    [Number(userId)]
  );

  if (existingUsers.length === 0) {
    const error = new Error('User not found');
    error.status = 404;
    throw error;
  }
};

const validateUserIdParamMiddleware = async (req, _res, next) => {
  try {
    await validateUserIdParam(req.params.id);
    next();
  } catch (err) {
    next(err);
  }
};

const validateUserUpdate = async (req, _res, next) => {
  try {
    const { name, email, password, phone, role } = req.body;
    const validatedUserData = {};

    if (name !== undefined) {
      if (!name) {
        const error = new Error('name cannot be empty');
        error.status = 400;
        throw error;
      }
      validatedUserData.name = name;
    }

    if (email !== undefined) {
      if (!email) {
        const error = new Error('email cannot be empty');
        error.status = 400;
        throw error;
      }

      const { rows: existingUsers } = await db.query(
        'SELECT user_id FROM users WHERE email = $1 AND user_id <> $2 LIMIT 1',
        [email, Number(req.params.id)]
      );

      if (existingUsers.length > 0) {
        const error = new Error('Email already registered');
        error.status = 409;
        throw error;
      }

      validatedUserData.email = email;
    }

    if (password !== undefined) {
      if (!password) {
        const error = new Error('password cannot be empty');
        error.status = 400;
        throw error;
      }
      validatedUserData.password = password;
    }

    if (phone !== undefined) {
      validatedUserData.phone = phone || null;
    }

    if (role !== undefined) {
      if (!VALID_ROLES.has(role)) {
        const error = new Error(
          'role must be admin, mechanic, lead_mechanic, inspector, or clerk'
        );
        error.status = 400;
        throw error;
      }
      validatedUserData.role = role;
    }

    if (Object.keys(validatedUserData).length === 0) {
      const error = new Error('At least one field is required to update the user');
      error.status = 400;
      throw error;
    }

    req.validatedUserUpdateData = validatedUserData;

    next();
  } catch (err) {
    next(err);
  }
};

const validateUserUpdateRequest = async (req, _res, next) => {
  try {
    await validateUserIdParam(req.params.id);

    const { name, email, password, phone, role } = req.body;
    const validatedUserData = {};

    if (name !== undefined) {
      if (!name) {
        const error = new Error('name cannot be empty');
        error.status = 400;
        throw error;
      }
      validatedUserData.name = name;
    }

    if (email !== undefined) {
      if (!email) {
        const error = new Error('email cannot be empty');
        error.status = 400;
        throw error;
      }

      const { rows: existingUsers } = await db.query(
        'SELECT user_id FROM users WHERE email = $1 AND user_id <> $2 LIMIT 1',
        [email, Number(req.params.id)]
      );

      if (existingUsers.length > 0) {
        const error = new Error('Email already registered');
        error.status = 409;
        throw error;
      }

      validatedUserData.email = email;
    }

    if (password !== undefined) {
      if (!password) {
        const error = new Error('password cannot be empty');
        error.status = 400;
        throw error;
      }
      validatedUserData.password = password;
    }

    if (phone !== undefined) {
      validatedUserData.phone = phone || null;
    }

    if (role !== undefined) {
      if (!VALID_ROLES.has(role)) {
        const error = new Error(
          'role must be admin, mechanic, lead_mechanic, inspector, or clerk'
        );
        error.status = 400;
        throw error;
      }
      validatedUserData.role = role;
    }

    if (Object.keys(validatedUserData).length === 0) {
      const error = new Error('At least one field is required to update the user');
      error.status = 400;
      throw error;
    }

    req.validatedUserUpdateData = validatedUserData;

    next();
  } catch (err) {
    next(err);
  }
};

module.exports = {
  validateUserRegistration,
  validateUserLogin,
  validateUserUpdate,
  validateUserIdParam,
  validateUserIdParamMiddleware,
  validateUserUpdateRequest,
};
