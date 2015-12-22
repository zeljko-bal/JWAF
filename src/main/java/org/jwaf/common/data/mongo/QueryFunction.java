package org.jwaf.common.data.mongo;

import java.util.function.Function;

import org.mongodb.morphia.query.Query;

/**
 * A function that receives a preconfigured {@link Query} object, additionaly configures the query 
 * and returns the query back. Intended for use with lambda functions that add query clauses.
 * 
 * @author zeljko.bal
 *
 * @param <T> the type of the entity
 */
public interface QueryFunction<T> extends Function<Query<T>, Query<T>>
{}
