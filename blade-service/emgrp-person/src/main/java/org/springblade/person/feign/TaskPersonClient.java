package org.springblade.person.feign;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.person.entity.EmergMissingOperTask;
import org.springblade.person.entity.EmergrpPersonDataInfo;
import org.springblade.person.enums.DataStatusEnum;
import org.springblade.person.enums.DataType;
import org.springblade.person.enums.OperTaskEnum;
import org.springblade.person.service.*;
import org.springblade.person.util.LocalDateTimeUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 新增失联人员定位执行任务
 *
 * @author wyl
 */
@RestController
@AllArgsConstructor
@Slf4j
public class TaskPersonClient implements ITaskPersonClient {
    //新增失联任务
	private IEmergMissingOperTaskService iEmergMissingOperTaskService;

    //数据信息
	private IEmergrpPersonDataInfoService iPersonDataInfoService;
	//数据存储
	private IdataSaveDbService dataSaveDbService;

	private IEmergrpAccidentBaseStationService accidentBaseStationService;

	private IEmergAccidentRuleService accidentRuleService;

	private IEmergrpPersonDataInfoService personDataInfoService;

//	private IDataAnalyzeService dataAnalyzeService;

	@Override
	public R<List<EmergMissingOperTask>> getPersonTask(String eventId) {
		if(StringUtil.isEmpty(eventId)){
			return R.fail("传入的事件ID，eventId不能为空");
		}
		EmergMissingOperTask emergMissingOperTaskEntry =new EmergMissingOperTask();
		emergMissingOperTaskEntry.setEventId(Long.parseLong(eventId));
		List<EmergMissingOperTask> list= iEmergMissingOperTaskService.list(Condition.getQueryWrapper(emergMissingOperTaskEntry));
		return R.data(list);
	}

	// 新增失联人员定位执行任务
	@Override
	@PostMapping(ADD_TSAK_PERSON)
	public R<Boolean> addTsakPerson(String taskId, String taskName, String eventId, String eventName, String createUser) {

		if (StringUtil.isEmpty(taskId)){
			return R.fail("传入的taskId不能为空！");
		}
		EmergMissingOperTask emergMissingOperTaskEntry =new EmergMissingOperTask();
		emergMissingOperTaskEntry.setTaskId(Long.valueOf(taskId));
		emergMissingOperTaskEntry.setTaskName(taskName);
		emergMissingOperTaskEntry.setEventId(Long.parseLong(eventId));
		emergMissingOperTaskEntry.setEventName(eventName);
		emergMissingOperTaskEntry.setCreateUser(Long.valueOf(createUser));
		emergMissingOperTaskEntry.setStatus(OperTaskEnum.WAIT.getValue());
		emergMissingOperTaskEntry.setCreateTime(LocalDateTime.now());
		emergMissingOperTaskEntry.setStartTime(LocalDateTime.now());
		return R.data(iEmergMissingOperTaskService.save(emergMissingOperTaskEntry));
	}

	@Override
	public R<Boolean> delPersonTask(Collection<Long> idLis) {
		QueryWrapper<EmergMissingOperTask> wrapper = new QueryWrapper<>();
		wrapper.in("taskId", idLis);
		return R.data(iEmergMissingOperTaskService.remove(wrapper));
	}


	//修改失联人员定位执行任务
	@Override
	@PostMapping(UPDATA_TASK_PERSON)
	public R<Boolean> updataPersonTask(String taskId, String startTime, String memo, String status,
									   String progress, String updateUser,
									   String eventId) {
		//Wrapper<Count>
		if (StringUtil.isEmpty(taskId)){
			return R.fail("传入的taskId不能为空！");
		}

		EmergMissingOperTask queue = new EmergMissingOperTask();
		queue.setTaskId(Long.parseLong(taskId));

		EmergMissingOperTask emergMissingOperTask = iEmergMissingOperTaskService.getOne(Condition.getQueryWrapper(queue));
		if(emergMissingOperTask ==null){
			return R.fail("没有找到对应的失联人员定位任务！");
		}
		if (!StringUtil.isEmpty(startTime)){
			emergMissingOperTask.setStartTime(LocalDateTimeUtil.strPaseLocalDateTime(startTime,"yyyy-MM-dd HH:mm:ss"));
		}
		if (!StringUtil.isEmpty(memo)){
			emergMissingOperTask.setMemo(memo);
		}
		if (!StringUtil.isEmpty(status)){
			emergMissingOperTask.setStatus(Integer.parseInt(status));
		}
		if (!StringUtil.isEmpty(progress)){

			BigDecimal prog = new BigDecimal(progress);
			prog=prog.setScale(2, BigDecimal.ROUND_DOWN); //小数bai位du 直接舍去
			emergMissingOperTask.setProgress(prog);

		}
		if (!StringUtil.isEmpty(updateUser)){
			emergMissingOperTask.setUpdateUser(Long.parseLong(updateUser));
		}
		if (!StringUtil.isEmpty(eventId)){
			emergMissingOperTask.setEventId(Long.parseLong(eventId));
		}
		emergMissingOperTask.setUpdateTime(LocalDateTime.now());

		return R.data(iEmergMissingOperTaskService.save(emergMissingOperTask));
	}
	// 新增数据信息
	@Override
	@PostMapping(ADD_PERSON_DATA)
	public R<Boolean> addPersonData(String taskId, String dataType, String bucketName, String objectName, String originalFilename) {


		if (StringUtil.isEmpty(taskId)){
			return R.fail("新增数据信息的taskId不能为空!");
		}
		EmergrpPersonDataInfo entry =new EmergrpPersonDataInfo();

		QueryWrapper<EmergMissingOperTask> rersonDataWrapper = new QueryWrapper<>();
		rersonDataWrapper.eq("taskId",taskId);
		EmergMissingOperTask emergMissingOperTaskEntry =iEmergMissingOperTaskService.getOne(rersonDataWrapper);

		if (null == emergMissingOperTaskEntry ||
			emergMissingOperTaskEntry.getStatus()==OperTaskEnum.EXEC.getValue()||
			emergMissingOperTaskEntry.getStatus()==OperTaskEnum.EXEC_SUCC.getValue()){
			return R.fail("不存在失联人员分析任务，或该任务在执行中,或已完成，不能导入信令数据！");
		}
		QueryWrapper<EmergrpPersonDataInfo> dataWrapper = new QueryWrapper<>();
		dataWrapper.eq("taskId",taskId).eq("dataType",dataType).eq("status",DataStatusEnum.NO_PARSED.getValue());
		List<EmergrpPersonDataInfo> personDataInfoList = iPersonDataInfoService.list(dataWrapper);
		if (personDataInfoList.size()>0){
			personDataInfoList.forEach(temp->{
				temp.setStatus(DataStatusEnum.PARSED.getValue());
			});
			iPersonDataInfoService.saveOrUpdateBatch(personDataInfoList);
		}
		String dataName = "";
		if (null!= DataType.getValueName(emergMissingOperTaskEntry.getTaskName(),Integer.parseInt(dataType))){
			dataName = DataType.getValueName(emergMissingOperTaskEntry.getTaskName(),Integer.parseInt(dataType));
		}
		entry.setTaskId(Long.valueOf(taskId));
		entry.setDataType(Integer.parseInt(dataType));
		entry.setDataName(dataName);
		entry.setBucketName(bucketName);
		entry.setFileName(objectName);
		entry.setStatus(DataStatusEnum.NO_PARSED.getValue());
		entry.setCreateTime(LocalDateTime.now());
		//entry.setOriginalFileName(originalFilename);
		boolean flag = iPersonDataInfoService.save(entry);

		if (flag&&entry.getDataType()==DataType.PERSONNEL_CONTROL.getValue()){
            //储存文件到 本地硬盘
			dataSaveDbService.doMoveFileToDisk(taskId,DataType.PERSONNEL_CONTROL,bucketName,objectName);
		}
		if (flag&&entry.getDataType()==DataType.PERSONNEL_PERSON.getValue()){

			//储存文件到 本地硬盘
			dataSaveDbService.doMoveFileToDisk(taskId,DataType.PERSONNEL_PERSON,bucketName,objectName);
		}

		if(flag&&entry.getDataType()==DataType.BASE_STATION.getValue()){

			try {
				//基站数据入库
				accidentBaseStationService.saveBaseStation(taskId);
			}catch (Exception e){
				log.error("导入基站数据失败！");
			}
		}
		if(flag&&entry.getDataType()==DataType.PERSON_INFO.getValue()){
			try {
				//用户基本信息入库
				personDataInfoService.saveData(taskId);
				//dataAnalyzeService.personInfoData(taskId);
			}catch (Exception e){
				log.error("导入用户基本信息出错!");
			}
		}
		return R.data(true);
	}

	/**
	 * 获取疑似失联任务
	 *
	 * @param taskId
	 * @return
	 */
	@Override
	public R<List<EmergMissingOperTask>> getPersonTaskByTaskId(String taskId) {
		if(StringUtil.isEmpty(taskId)){
			return R.fail("传入的任务ID，taskId不能为空");
		}
		EmergMissingOperTask emergMissingOperTaskEntry = new EmergMissingOperTask();
		emergMissingOperTaskEntry.setTaskId(Long.parseLong(taskId));
		List<EmergMissingOperTask> list = iEmergMissingOperTaskService.list(Condition.getQueryWrapper(emergMissingOperTaskEntry));
		return R.data(list);
	}

	/**
	 * 根据任务id批量修改疑是失联人员信息
	 *
	 * @param taskId
	 * @param taskName
	 * @param eventId
	 * @param eventName
	 * @param updateUser
	 * @return
	 */
	@Override
	public R<Boolean> updatePersonTaskByTaskId(String taskId, String taskName, String eventId, String eventName, String updateUser) {
		if (StringUtil.isEmpty(taskId)){
			return R.fail("传入的taskId不能为空！");
		}
		List<EmergMissingOperTask> taskEntries = this.getPersonTaskByTaskId(taskId).getData();
		for (EmergMissingOperTask taskEntry:taskEntries) {
			taskEntry.setTaskId(Long.valueOf(taskId));
			taskEntry.setTaskName(taskName);
			taskEntry.setEventId(Long.parseLong(eventId));
			taskEntry.setEventName(eventName);
			taskEntry.setUpdateUser(Long.valueOf(updateUser));
		}
		return R.data(iEmergMissingOperTaskService.updateBatchById(taskEntries));
	}
}
