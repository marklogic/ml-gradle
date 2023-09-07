'use strict';
const op = require('/MarkLogic/optic');

op.fromView('Medical', 'Authors').generateView('alternate', 'authors')
