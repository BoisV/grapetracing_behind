package cn.edu.scau.service.impl;

import cn.edu.scau.dao.AreaDao;
import cn.edu.scau.dao.TreeDao;
import cn.edu.scau.model.SearchTreeForm;
import cn.edu.scau.model.Tree;
import cn.edu.scau.model.TreeCategory;
import cn.edu.scau.service.ITreeService;
import cn.edu.scau.util.FastDFSClientUtil;
import cn.edu.scau.util.qrcode.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class TreeServiceImpl implements ITreeService {
    @Autowired
    private TreeDao treeDao;

    @Autowired
    private AreaDao areaDao;

    @Autowired
    private FastDFSClientUtil dfsClient;

    @Override
    public List<Tree> getAllTrees() {
        return treeDao.selectAll();
    }

    @Override
    public List<TreeCategory> getAllCategory() {
        List<TreeCategory> categories = treeDao.selectAllCategory();
        for (TreeCategory category : categories) {
            category.count = treeDao.selectCountByCategory(category.name);
            category.alive = treeDao.selectCountByStatus("存活");
            category.dead = treeDao.selectCountByStatus("死亡");
        }
        return categories;
    }

    @Override
    public void addTrees(Tree tree) {
        /*for (int i = 0; i < tree.count; i++) {
            treeDao.insertOne(tree);
        }*/
        treeDao.insertMany(tree);
    }

    @Override
    public void removeTrees(long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            treeDao.deleteById(ids[i]);
        }
    }

    @Override
    public void editTree(Tree tree) {
        treeDao.updateOneById(tree);
    }

    @Override
    public List<Tree> findTrees(SearchTreeForm form) {
        List<Tree> trees = null;
        switch (form.getMode()) {
            case 1:
                trees = treeDao.selectTreeByPeriod(form.getStart_date(), form.getEnd_date());
                break;
            case 2:
                trees = treeDao.selectTreeByAreaId(form.getArea_id());
                break;
            case 3:
                trees = treeDao.selectTreeByAreaIdAndPeriod(form.getArea_id(), form.getStart_date(), form.getEnd_date());
                break;
            case 4:
                Tree tree = treeDao.selectTreeById(form.getId());
                trees = new ArrayList<>();
                if (tree != null)
                    trees.add(tree);
                break;
            case 8:
                trees = treeDao.selectTreeByStatus(form.getStatus());
                break;
            case 9:
                trees = treeDao.selectTreeByStatusAndPeriod(form.getStatus(), form.getStart_date(), form.getEnd_date());
                break;
            case 10:
                trees = treeDao.selectTreeByAreaIdAndStatus(form.getArea_id(), form.getStatus());
                break;
            case 11:
                trees = treeDao.selectTreeByAreaIdAndPeriodAndStatus(form.getArea_id(), form.getStart_date(), form.getEnd_date(), form.getStatus());
                break;
            default:
                break;
        }
        if (trees == null) {
            trees = new ArrayList<>();
        }
        return trees;
    }

    @Override
    public String getTreeQRCode(Integer id) throws Exception {
        MultipartFile multipartFile = null;
        String basePath = ResourceUtils.getURL("classpath:").getPath();
        String fullPath = basePath + "static/tree.jpg";
        String info = "treeId=" + String.valueOf(id);
        multipartFile = QRCodeUtil.encodeToMultipartFile(info, fullPath, true);

        String fileUrl = dfsClient.uploadFile(multipartFile);
        return fileUrl;
    }
}
