package org.jwaf.common.data.mongo;

import java.util.function.Function;

import org.mongodb.morphia.query.Query;

public interface QueryFunction<T> extends Function<Query<T>, Query<T>>
{}
