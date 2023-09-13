package jason.hermes.bioinspired.criogenic;

import jason.asSemantics.Agent;
import jason.hermes.bioinspired.AslFileGenerator;
import jason.hermes.bioinspired.AslTransferenceModel;
import jason.hermes.exception.ErrorCreatingFileException;
import jason.hermes.exception.ErrorCreatingFolderException;
import jason.hermes.exception.ErrorWritingFileContentException;
import jason.hermes.utils.FileUtils;

import java.io.File;

public class MasBuilderContent {

	public static File buildMas(MasBuilderStructure masBuilderStructure, String masPath) {
		File masFile = null;
		File masFolder = new File(FileUtils.getCryogenatedMasPath(masPath, masBuilderStructure.getMasName()));

		if (FileUtils.createFolder(masFolder)) {
			File masStructureFile = new File(masFolder, masBuilderStructure.getMasName() + FileUtils.MAS_STRUCTURE_FILE_EXTENSION);
			if (FileUtils.createFile(masStructureFile)) {
				File agentFolder = new File(masFolder, FileUtils.AGENT_FOLDER);
				if(FileUtils.createFolder(agentFolder)) {
					if (FileUtils.writeFileContent(masStructureFile, masBuilderStructure.getCompleteStructure(agentFolder.getPath()))) {
						for (Agent agent : masBuilderStructure.getAgents()) {
							try {
								AslTransferenceModel aslTransferenceModel = AslFileGenerator
										.generateAslContentWithoutCryogenicIntention(agent.getTS().getAgArch());
								AslFileGenerator.createAslFile(agentFolder.getPath() + File.separator
										+ aslTransferenceModel.getName() + AslFileGenerator.ASL_EXTENSION,
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
