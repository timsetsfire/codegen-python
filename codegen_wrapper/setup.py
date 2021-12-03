import setuptools

with open("../README.md", "r") as fh:
    long_description = fh.read()

setuptools.setup(
    name="datarobot-codegen-wrapper", # Replace with your own username
    version="0.3.0",
    author="tim whittaker",
    author_email="timothy.whittaker@datarobot.com",
    description="python wrapper for datarobot codegen models",
    long_description=long_description,
    long_description_content_type="text/markdown",
    # url="https://github.com/pypa/sampleproject",
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
    ],
    python_requires='>=3.6',
    include_package_data=True,
    packages=setuptools.find_packages(),
    package_data={
        "": ["*.json", "*.jar", "*.R", "*.j2"],
    },
)