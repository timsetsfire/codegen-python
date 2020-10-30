.PHONY: wheel

dist: java_components wheel

java_components:
	cd ./codegen_wrapper/gateway && $(MAKE)

wheel:
	cd ./codegen_wrapper && $(MAKE)

# clean:
# 	\rm -rf dist build datarobot_drum.egg-info
# 	find . -type d -name __pycache__ | xargs rm -rf
# 	find . -name *.pyc -delete