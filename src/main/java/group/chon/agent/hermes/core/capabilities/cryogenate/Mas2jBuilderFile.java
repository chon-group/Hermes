package group.chon.agent.hermes.core.capabilities.cryogenate;

import jason.asSemantics.Agent;
import group.chon.agent.hermes.core.exception.ErrorCreatingFileException;
import group.chon.agent.hermes.core.exception.ErrorCreatingFolderException;
import group.chon.agent.hermes.core.exception.ErrorWritingFileContentException;
import group.chon.agent.hermes.core.utils.FileUtils;
import group.chon.agent.hermes.core.utils.aslFiles.AslFileGeneratorUtils;
import group.chon.agent.hermes.core.utils.aslFiles.AslTransferenceModel;

import java.io.File;

public abstract class Mas2jBuilderFile {

	public static File buildMas(Mas2jBuilderStructure mas2jBuilderStructure, String masPath) {
		File masFile = null;
		File masFolder = new File(FileUtils.getCryogenatedMasPath(masPath, mas2jBuilderStructure.getMasName()));

		if (FileUtils.createFolder(masFolder)) {
			File masStructureFile = new File(masFolder, mas2jBuilderStructure.getMasName() + FileUtils.MAS_STRUCTURE_FILE_EXTENSION);
			if (FileUtils.createFile(masStructureFile)) {
				File agentFolder = new File(masFolder, FileUtils.AGENT_FOLDER);
				if(FileUtils.createFolder(agentFolder)) {
					if (FileUtils.writeFileContent(masStructureFile, mas2jBuilderStructure.getCompleteStructure(agentFolder.getPath()))) {
						for (Agent agent : mas2jBuilderStructure.getAgents()) {
							try {
								AslTransferenceModel aslTransferenceModel = AslFileGeneratorUtils
										.generateAslContent(agent.getTS().getAgArch());
								AslFileGeneratorUtils.createAslFile(agentFolder.getPath() + File.separator
										+ aslTransferenceModel.getName() + AslFileGeneratorUtils.ASL_EXTENSION,
										aslTransferenceModel);
							} catch (Exception e){
								throw new ErrorCreatingFileException(agent.getTS().getAgArch().getAgName());
							}
						}
						masFile = masStructureFile;
					} else {
						throw new ErrorWritingFileContentException(masStructureFile.getName());
					}
				} else {
					throw new ErrorCreatingFolderException(agentFolder.getName());
				}
			} else {
				throw new ErrorCreatingFileException(masStructureFile.getName());
			}
		} else {
			throw new ErrorCreatingFolderException(masFolder.getName());
		}

		return masFile;
		
	}

}
