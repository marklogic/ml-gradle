'use strict';
const op = require('/MarkLogic/optic');

op.fromView('Medical', 'Books').generateView('alternate', 'books');
