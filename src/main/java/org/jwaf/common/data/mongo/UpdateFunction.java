package org.jwaf.common.data.mongo;

import java.util.function.Function;

import org.mongodb.morphia.query.UpdateOperations;

/**
 * Like {@link QueryFunction}, but for {@link UpdateOperations}.
 * 
 * @author zeljko.bal
 *
 * @param <T>
 */
public interface UpdateFunction<T> extends Function<UpdateOperations<T>, UpdateOperations<T>>
{}