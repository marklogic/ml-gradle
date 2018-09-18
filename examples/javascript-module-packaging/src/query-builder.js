import castArray from 'lodash-es/castArray';
import map from 'lodash-es/map';

export class QueryBuilder {
	constructor() {
		this.query = cts.trueQuery();
	}
	and(query) {
		this.query = cts.andQuery([
			this.query,
			...map(castArray(query), q => ('string' === typeof q ? cts.parse(q) : q))
		]);
		return this;
	}
	build() {
		if ('development' === process.env.NODE_ENV) {
			console.log(this.query);
		}
		return this.query;
	}
}
