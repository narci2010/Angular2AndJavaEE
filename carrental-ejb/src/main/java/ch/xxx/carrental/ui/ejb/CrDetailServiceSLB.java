/**
 *    Copyright 2016 Sven Loesekann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.carrental.ui.ejb;

import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ch.xxx.carrental.ui.dto.CrDetail;
import ch.xxx.carrental.ui.model.CrDetailDB;
import ch.xxx.carrental.ui.model.CrPeriodDB;
import ch.xxx.carrental.ui.model.CrPortfolioDB;
import ch.xxx.carrental.ui.service.CrDetailService;

@Local(CrDetailService.class)
@Stateless
public class CrDetailServiceSLB implements CrDetailService {

	@EJB
	private CrServerSIB server;
	@PersistenceContext
	private EntityManager em;

	private Converter conv = new Converter();

	@AutoLogging
	@Override
	public CrDetail readCrDetail(String mietNr, String jahr, Locale locale) {
		if (Utils.checkForWildflyorWS()) {
			List<CrDetailDB> resultList = em
					.createQuery("select c from CrDetailDB c where c.mietNr=:mietNr and c.jahr=:jahr", CrDetailDB.class)
					.setParameter("mietNr", mietNr).setParameter("jahr", jahr).getResultList();
			CrDetail lsdDetail = resultList.isEmpty() ? null : conv.convert((resultList.get(0)));
			return lsdDetail;
		}
		return server.readCrDetail(mietNr, jahr);
	}

	@AutoLogging
	@Override
	public boolean createCrDetail(CrDetail crDetail) {
		if (Utils.checkForWildflyorWS()) {
			CrDetailDB crDetailDB = new CrDetailDB();
			CrPeriodDB crPeriodDB = new CrPeriodDB();
			crDetailDB.getCrPeriods().add(crPeriodDB);
			CrPortfolioDB crPortfolioDB = new CrPortfolioDB();			
			crPeriodDB.getCrPortfolios().add(crPortfolioDB);
			conv.convert(crDetail, crDetailDB);
			em.persist(crDetailDB);
			return true;
		}
		return server.createCrDetail(crDetail);
	}

	@AutoLogging
	@Override
	public boolean updateCrDetail(CrDetail crDetail) {
		if (Utils.checkForWildflyorWS()) {
			List<CrDetailDB> resultList = em
					.createQuery("select c from CrDetailDB c where c.mietNr=:mietNr and c.jahr=:jahr", CrDetailDB.class)
					.setParameter("mietNr", crDetail.getMietNr()).setParameter("jahr", crDetail.getJahr()).getResultList();			
			return resultList.isEmpty() ? false : conv.convert(crDetail, (resultList.get(0)));
		}
		return server.updateCrDetail(crDetail);
	}

	@AutoLogging
	@Override
	public boolean deleteCrDetail(String mietNr, String jahr) {
		if (Utils.checkForWildflyorWS()) {
			List<CrDetailDB> resultList = em
					.createQuery("select c from CrDetailDB c where c.mietNr=:mietNr and c.jahr=:jahr", CrDetailDB.class)
					.setParameter("mietNr", mietNr).setParameter("jahr", jahr).getResultList();
			if(resultList.isEmpty()) {
				return false;
			}
			em.remove(resultList.get(0));
			return true;
		}
		return server.deleteCrDetail(mietNr, jahr);
	}

}
