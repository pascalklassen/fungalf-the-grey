package io.github.pascalklassen.fungalf.persistence.entity

import io.vertx.core.Future

interface EntityRetriever<IdentifierT, EntityT> {

    fun getById(id: IdentifierT): Future<EntityT>
}
