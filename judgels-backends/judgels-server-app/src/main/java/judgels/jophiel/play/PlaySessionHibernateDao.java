package judgels.jophiel.play;

import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;

public class PlaySessionHibernateDao extends UnmodifiableHibernateDao<PlaySessionModel> implements PlaySessionDao {
    @Inject
    public PlaySessionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<PlaySessionModel> getByAuthCode(String authCode) {
        return selectByUniqueColumn(PlaySessionModel_.authCode, authCode);
    }
}
