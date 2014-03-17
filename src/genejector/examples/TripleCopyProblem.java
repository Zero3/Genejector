package genejector.examples;

import genejector.project.GenejectorProblem;

public class TripleCopyProblem implements GenejectorProblem
{
	// [ListingStart][L2]
	// These 3 inputs ...
	private Integer input1 = null;
	private Integer input2 = null;
	private Integer input3 = null;

	// ... should be copied into these 3 outputs
	private Integer output1 = null;
	private Integer output2 = null;
	private Integer output3 = null;

	public void setInputs(Integer input1, Integer input2, Integer input3)
	{
		this.input1 = input1;
		this.input2 = input2;
		this.input3 = input3;
	}

	@Override
	public void resetState()
	{
		output1 = null;
		output2 = null;
		output3 = null;
	}
	// [ListingEnd][L2]
	public Integer getInput1()
	{
		return input1;
	}

	public Integer getOutput1()
	{
		return output1;
	}

	public void setOutput1(Integer output1)
	{
		this.output1 = output1;
	}

	public Integer getInput2()
	{
		return input2;
	}

	public Integer getOutput2()
	{
		return output2;
	}

	public void setOutput2(Integer output2)
	{
		this.output2 = output2;
	}

	public Integer getInput3()
	{
		return input3;
	}

	public Integer getOutput3()
	{
		return output3;
	}

	public void setOutput3(Integer output3)
	{
		this.output3 = output3;
	}
}