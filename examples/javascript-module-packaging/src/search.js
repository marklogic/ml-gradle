import { QueryBuilder } from './query-builder.js';
const jsearch = require('/MarkLogic/jsearch');

export default function search(query) {
	return jsearch
		.documents()
		.where(
			new QueryBuilder()
				.and(query)
				.and(cts.collectionQuery(['staging']))
				.build()
		)
		.result();
}
