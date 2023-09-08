'use strict';
const op = require('/MarkLogic/optic');
op.fromView('HR', 'employees')
	.where(op.eq(op.col('Department'), "Engineering"))
	.generateView('HR', 'Engineering');
