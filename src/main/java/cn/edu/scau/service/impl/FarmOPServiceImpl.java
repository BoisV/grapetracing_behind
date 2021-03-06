package cn.edu.scau.service.impl;

import cn.edu.scau.dao.FarmOPDao;
import cn.edu.scau.dao.TreeDao;
import cn.edu.scau.dao.UserDao;
import cn.edu.scau.model.FarmOP;
import cn.edu.scau.model.FarmOPCategory;
import cn.edu.scau.model.SearchFarmOPForm;
import cn.edu.scau.model.Tree;
import cn.edu.scau.service.IFarmOPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FarmOPServiceImpl implements IFarmOPService {
    @Autowired
    FarmOPDao farmOPDao;

    @Autowired
    TreeDao treeDao;

    @Autowired
    UserDao userDao;

    public List<FarmOP> getAllOPs() {
        return farmOPDao.selectAllWithUsername();
    }

    public List<FarmOPCategory> getAllFarmCate() {
        return farmOPDao.selectAllFarmCate();
    }

    public boolean addOneFarmopForArea(FarmOP farmOP) {
        List<Tree> trees = treeDao.selectTreeByAreaId(farmOP.getArea_id());
        int success = 0;
        for (Tree tree : trees) {
            farmOP.setTree_id(tree.getId());
            success += farmOPDao.insertOneFarmop(farmOP);
        }
        return success == trees.size();
    }

    public int addOneFarmopForTree(FarmOP farmOP) {
        farmOP.setArea_id(treeDao.selectTreeById(farmOP.getTree_id()).getArea_id());
        return farmOPDao.insertOneFarmop(farmOP);
    }

    public int editOneFarmop(FarmOP farmOP) {
        return farmOPDao.updateOne(farmOP);
    }

    public int delete(List<Long> ids) {
        int i = 0;
        for (; i < ids.size(); i++) {
            farmOPDao.deleteById(ids.get(i));
        }
        return i;
    }

    @Override
    public List<FarmOP> findFarmOPs(SearchFarmOPForm form) {
        List<FarmOP> farmOPS = null;
        switch (form.getMode()) {
            case 1:
                farmOPS = farmOPDao.selectManyByPeriod(form.getStart_date(), form.getEnd_date());
                break;
            case 2:
                farmOPS = farmOPDao.selectManyByAreaId(form.getArea_id());
                break;
            case 3:
                farmOPS = farmOPDao.selectManyByPeriodAndAreaId(form.getStart_date(), form.getEnd_date(), form.getArea_id());
                break;
            case 4:
                farmOPS = farmOPDao.selectManyByTreeId(form.getTree_id());
                break;
            case 5:
                farmOPS = farmOPDao.selectManyByPeriodAndTreeId(form.getStart_date(), form.getEnd_date(), form.getTree_id());
                break;
            case 8:
                farmOPS = farmOPDao.selectManyByUserId(form.getUser_id());
                break;
            case 9:
                farmOPS = farmOPDao.selectManyByPeriodAndUserId(form.getStart_date(), form.getEnd_date(), form.getUser_id());
                break;
            case 10:
                farmOPS = farmOPDao.selectManyByAreaIdAndUserId(form.getArea_id(), form.getUser_id());
                break;
            case 11:
                farmOPS = farmOPDao.selectManyByPeriodAndAreaIdAndUserId(form.getStart_date(), form.getEnd_date(), form.getArea_id(), form.getUser_id());
                break;
            case 12:
                farmOPS = farmOPDao.selectManyByTreeIdAndUserId(form.getTree_id(), form.getUser_id());
                break;
            case 13:
                farmOPS = farmOPDao.selectManyByPeriodAndTreeIdAndUserId(form.getStart_date(), form.getEnd_date(), form.getTree_id(), form.getUser_id());
                break;
            default:
                break;
        }
        if (farmOPS == null)
            farmOPS = new ArrayList<>();
        return farmOPS;
    }
}
