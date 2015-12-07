package org.jwaf.common.mongo;

import java.util.function.Function;

import org.mongodb.morphia.query.UpdateOperations;

public interface UpdateFunction<T> extends Function<UpdateOperations<T>, UpdateOperations<T>>
{}