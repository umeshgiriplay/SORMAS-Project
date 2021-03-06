/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.region;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.region.DistrictCriteria;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.InfrastructureDataImporter;
import de.symeda.sormas.backend.util.InfrastructureDataImporter.DistrictConsumer;



@Stateless
@LocalBean
public class DistrictService extends AbstractAdoService<District> {
	
	public DistrictService() {
		super(District.class);
	}
	
	public List<District> getAllWithoutEpidCode() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<District> cq = cb.createQuery(getElementClass());
		Root<District> from = cq.from(getElementClass());
		cq.where(cb.isNull(from.get(District.EPID_CODE)));
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}
	
	public List<District> getAllByRegion(Region region) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<District> cq = cb.createQuery(getElementClass());
		Root<District> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(District.REGION), region));
		cq.orderBy(cb.asc(from.get(District.NAME)));

		return em.createQuery(cq).getResultList();
	}
	
	public int getCountByRegion(Region region) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<District> from = cq.from(getElementClass());
		cq.select(cb.count(from));
		cq.where(cb.equal(from.get(District.REGION), region));
		
		return em.createQuery(cq).getSingleResult().intValue();
	}

	public List<District> getByName(String name, Region region) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<District> cq = cb.createQuery(getElementClass());
		Root<District> from = cq.from(getElementClass());
	
		Predicate filter = cb.equal(cb.upper(from.get(District.NAME)), name.toUpperCase());
		if (region != null) {
			filter = cb.and(filter, cb.equal(from.get(District.REGION), region));
		}

		cq.where(filter);
		
		return em.createQuery(cq).getResultList();
	}
	
	public List<Long> getIdsByReferenceDtos(List<DistrictReferenceDto> references) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<District> from = cq.from(getElementClass());
		
		cq.where(from.get(District.UUID).in(references.stream().map(DistrictReferenceDto::getUuid).collect(Collectors.toList())));
		cq.select(from.get(District.ID));
		return em.createQuery(cq).getResultList();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<District, District> from, User user) {
		// no filter by user needed
		return null;
	}	

	public void importDistricts(String countryName, List<Region> regions) {
		InfrastructureDataImporter.importDistricts(countryName, new DistrictConsumer() {
			
			private Region cachedRegion = null;
			
			@Override
			public void consume(String regionName, String districtName, String epidCode, Float growthRate) {
				if (cachedRegion == null || !cachedRegion.getName().equals(regionName)) {
					Optional<Region> regionResult = regions.stream()
							.filter(r -> r.getName().equals(regionName))
							.findFirst();

					if (regionResult.isPresent()) {
						cachedRegion = regionResult.get();
					} else {
						logger.warn("Could not find region '" + regionName + "' for district '" + districtName + "'");
						return;
					}
					
					if (cachedRegion.getDistricts() == null) {
						cachedRegion.setDistricts(new ArrayList<District>());
					}
				}
				Optional<District> districtResult = cachedRegion.getDistricts().stream()
						.filter(r -> r.getName().equals(districtName))
						.findFirst();
				
				District district;
				if (districtResult.isPresent()) {
					district = districtResult.get();
				} else {
					district = new District();
					cachedRegion.getDistricts().add(district);
					district.setName(districtName);
					district.setRegion(cachedRegion);
				}
				
				district.setEpidCode(epidCode);
				district.setGrowthRate(growthRate);

				persist(district);
			}
		});
	}
	
	public Predicate buildCriteriaFilter(DistrictCriteria criteria, CriteriaBuilder cb, Root<District> from) {
		Predicate filter = null;
		if (criteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(from.join(District.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getNameEpidLike() != null) {
			String[] textFilters = criteria.getNameEpidLike().split("\\s+");
			for (int i=0; i<textFilters.length; i++)
			{
				String textFilter = "%" + textFilters[i].toLowerCase() + "%";
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
							cb.like(cb.lower(from.get(District.NAME)), textFilter),
							cb.like(cb.lower(from.get(District.EPID_CODE)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		return filter;
	}
}
