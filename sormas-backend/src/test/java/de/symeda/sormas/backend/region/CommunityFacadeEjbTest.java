package de.symeda.sormas.backend.region;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

public class CommunityFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetAllAfter() throws InterruptedException {
		
		Region region = creator.createRegion("region");
		District district = creator.createDistrict("district", region);
		creator.createCommunity("community1", district);
		getCommunityService().doFlush();
		Date date = new Date();
		List<CommunityDto> results = getCommunityFacade().getAllAfter(date);

		// List should be empty
		assertEquals(0, results.size());

		Thread.sleep(1); // delay to ignore known rounding issues in change date filter
		String communityName = "community2";
		creator.createCommunity(communityName, district);
		results = getCommunityFacade().getAllAfter(date);

		// List should have one entry
		assertEquals(1, results.size());

		assertEquals(communityName, results.get(0).getName());
		assertEquals(district.getUuid(), results.get(0).getDistrict().getUuid());
		assertEquals(region.getUuid(), results.get(0).getRegion().getUuid());
	}

}
