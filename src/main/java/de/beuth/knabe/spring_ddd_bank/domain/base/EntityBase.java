package de.beuth.knabe.spring_ddd_bank.domain.base;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Base class for an entity, as explained in the book "Domain Driven Design".
 * All entities in this project have an identity attribute with type Long and
 * name id. Inspired by the DDD Sample project.
 * 
 * @author Christoph Knabe
 * @since 2017-03-06
 * @see <a href=
 *      "https://github.com/citerus/dddsample-core/blob/master/src/main/java/se/citerus/dddsample/domain/shared/Entity.java">Entity
 *      in the DDD Sample</a>
 */
@MappedSuperclass
public abstract class EntityBase<T extends EntityBase<T>> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Returns the identity of this entity object.
	 * 
	 * @return the identity of this entity object
	 * 
	 * @deprecated You should not pass this identity to the interface layer, if you
	 *             can identify an entity by a domain key. You may use it in the
	 *             domain layer, as accesses by identity are more efficient.
	 *             2018-10-05 Knabe
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Entities compare by identity, not by attributes.
	 *
	 * @param that
	 *            The other entity of the same type
	 * @return true if the identities are the same, regardless of the other
	 *         attributes.
	 * @throws IllegalStateException
	 *             one of the entities does not have the identity attribute set.
	 */
	public boolean sameIdentityAs(final T that) {
		return this.equals(that);
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof EntityBase)) {
			return false;
		}
		final EntityBase<?> that = (EntityBase<?>) object;
		_checkIdentity(this);
		_checkIdentity(that);
		return this.id.equals(that.getId());
	}

	/**
	 * Checks the passed entity, if it has an identity. It gets an identity only by
	 * saving.
	 * 
	 * @param entity
	 *            the entity to be checked
	 * @throws IllegalStateException
	 *             the passed entity does not have the identity attribute set.
	 */
	private void _checkIdentity(final EntityBase<?> entity) {
		if (entity.getId() == null) {
			throw new IllegalStateException("Identity missing in entity: " + entity);
		}
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

}
